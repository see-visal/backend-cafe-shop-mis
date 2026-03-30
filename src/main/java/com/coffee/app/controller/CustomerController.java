package com.coffee.app.controller;

import com.coffee.app.dto.request.ChangePasswordRequest;
import com.coffee.app.dto.request.NotificationPreferenceRequest;
import com.coffee.app.dto.request.OrderRequest;
import com.coffee.app.dto.request.PaymentRequest;
import com.coffee.app.dto.request.PromoValidateRequest;
import com.coffee.app.dto.request.RatingRequest;
import com.coffee.app.dto.request.UpdateProfileRequest;
import com.coffee.app.dto.response.OrderResponse;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.dto.response.PromoValidateResponse;
import com.coffee.app.dto.response.RatingResponse;
import com.coffee.app.dto.response.ReceiptResponse;
import com.coffee.app.dto.response.UserProfileResponse;
import com.coffee.app.service.AuditLogService;
import com.coffee.app.service.OrderService;
import com.coffee.app.service.PaymentService;
import com.coffee.app.service.PromoCodeService;
import com.coffee.app.service.RatingService;
import com.coffee.app.service.UserProfileService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping({"/api/customer"})
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'BARISTA')")
public class CustomerController {
   private final OrderService orderService;
   private final PaymentService paymentService;
   private final UserProfileService userProfileService;
   private final RatingService ratingService;
   private final PromoCodeService promoCodeService;
   private final AuditLogService auditLogService;
   private static final Map<String, CopyOnWriteArrayList<SseEmitter>> ORDER_EMITTERS = new ConcurrentHashMap();

   @GetMapping({"/profile"})
   public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
      return ResponseEntity.ok(this.userProfileService.getProfile(jwt.getSubject()));
   }

   @PutMapping({"/profile"})
   public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpdateProfileRequest request) {
      UserProfileResponse profile = this.userProfileService.updateProfile(jwt.getSubject(), request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_PROFILE", "UserProfile", profile.uuid(), "Updated customer profile");
      return ResponseEntity.ok(profile);
   }

   @PutMapping({"/profile/notifications"})
   public ResponseEntity<UserProfileResponse> updateNotificationPreference(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid NotificationPreferenceRequest request) {
      UserProfileResponse profile = this.userProfileService.updateNotificationPreference(jwt.getSubject(), request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_NOTIFICATION_PREFERENCE", "UserProfile", profile.uuid(), "Updated notification preference");
      return ResponseEntity.ok(profile);
   }

   @PutMapping({"/profile/password"})
   public ResponseEntity<Map<String, String>> changePassword(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid ChangePasswordRequest request) {
      if (!request.newPassword().equals(request.confirmPassword())) {
         throw new IllegalArgumentException("New password and confirmation password do not match.");
      } else if (request.currentPassword().equals(request.newPassword())) {
         throw new IllegalArgumentException("New password must be different from current password.");
      } else {
         this.userProfileService.changePassword(jwt.getSubject(), request.currentPassword(), request.newPassword());
         this.auditLogService.log(this.actorId(jwt), "CHANGE_PASSWORD", "UserProfile", jwt.getClaimAsString("uuid"), "Changed customer password");
         return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
      }
   }

   @PostMapping({"/orders"})
   public ResponseEntity<OrderResponse> placeOrder(@RequestBody @Valid OrderRequest request, @AuthenticationPrincipal Jwt jwt) {
      UUID userId = UUID.fromString(jwt.getClaimAsString("uuid"));
      OrderRequest securedRequest = new OrderRequest(userId, request.orderType(), request.tableNumber(), request.items(), request.notes(), request.promoCode(), request.discountAmount());
      OrderResponse order = this.orderService.placeOrder(securedRequest);
      this.auditLogService.log(this.actorId(jwt), "PLACE_ORDER", "Order", order.id().toString(), "Placed customer order");
      return ResponseEntity.status(201).body(order);
   }

   @GetMapping({"/orders"})
   public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
      UUID userId = UUID.fromString(jwt.getClaimAsString("uuid"));
      return ResponseEntity.ok(this.orderService.getOrdersByUser(userId));
   }

   @GetMapping({"/orders/page"})
   public ResponseEntity<Page<OrderResponse>> getMyOrdersPaged(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String status) {
      UUID userId = UUID.fromString(jwt.getClaimAsString("uuid"));
      PageRequest pageable = PageRequest.of(page, size, Sort.by(new String[]{"createdAt"}).descending());
      return ResponseEntity.ok(this.orderService.getOrdersByUserPaginated(userId, status, pageable));
   }

   @GetMapping({"/orders/{id}"})
   public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
      return ResponseEntity.ok(this.orderService.getOrder(id));
   }

   @GetMapping(
      value = {"/orders/{id}/stream"},
      produces = {"text/event-stream"}
   )
   public SseEmitter streamOrder(@PathVariable String id) {
      SseEmitter emitter = new SseEmitter(300000L);
      ((CopyOnWriteArrayList)ORDER_EMITTERS.computeIfAbsent(id, (k) -> new CopyOnWriteArrayList())).add(emitter);
      emitter.onCompletion(() -> this.removeEmitter(id, emitter));
      emitter.onTimeout(() -> this.removeEmitter(id, emitter));
      emitter.onError((ex) -> this.removeEmitter(id, emitter));

      try {
         OrderResponse current = this.orderService.getOrder(UUID.fromString(id));
         emitter.send(SseEmitter.event().name("status").data(current));
      } catch (IllegalArgumentException | IOException e) {
         emitter.completeWithError(e);
      }

      return emitter;
   }

   public static void pushOrderUpdate(String orderId, Object data) {
      CopyOnWriteArrayList<SseEmitter> emitters = (CopyOnWriteArrayList)ORDER_EMITTERS.get(orderId);
      if (emitters != null) {
         emitters.removeIf((emitter) -> {
            try {
               emitter.send(SseEmitter.event().name("status").data(data));
               return false;
            } catch (IOException e) {
               emitter.completeWithError(e);
               return true;
            }
         });
      }
   }

   private void removeEmitter(String orderId, SseEmitter emitter) {
      CopyOnWriteArrayList<SseEmitter> emitters = (CopyOnWriteArrayList)ORDER_EMITTERS.get(orderId);
      if (emitters != null) {
         emitters.remove(emitter);
      }

   }

   @DeleteMapping({"/orders/{id}"})
   public ResponseEntity<Void> cancelOrder(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      this.orderService.cancelOrder(id);
      this.auditLogService.log(this.actorId(jwt), "CANCEL_ORDER", "Order", id.toString(), "Cancelled customer order");
      return ResponseEntity.noContent().build();
   }

   @PostMapping({"/orders/{id}/rating"})
   public ResponseEntity<RatingResponse> rateOrder(@PathVariable UUID id, @RequestBody @Valid RatingRequest request, @AuthenticationPrincipal Jwt jwt) {
      UUID userId = UUID.fromString(jwt.getClaimAsString("uuid"));
      RatingResponse rating = this.ratingService.submitRating(id, userId, request);
      this.auditLogService.log(this.actorId(jwt), "SUBMIT_RATING", "Rating", rating.id().toString(), "Submitted customer rating for order " + id);
      return ResponseEntity.status(201).body(rating);
   }

   @PostMapping({"/payments"})
   public ResponseEntity<PaymentResponse> pay(@RequestBody @Valid PaymentRequest request, @AuthenticationPrincipal Jwt jwt) {
      PaymentResponse payment = this.paymentService.createPayment(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_PAYMENT", "Payment", payment.id().toString(), "Created payment for order " + payment.orderId());
      return ResponseEntity.status(201).body(payment);
   }

   @GetMapping({"/payments/order/{orderId}"})
   public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID orderId) {
      return ResponseEntity.ok(this.paymentService.getPaymentByOrder(orderId));
   }

   @GetMapping({"/orders/{orderId}/receipt"})
   public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable UUID orderId) {
      return ResponseEntity.ok(this.paymentService.getReceipt(orderId));
   }

   @PostMapping({"/promos/validate"})
   public ResponseEntity<PromoValidateResponse> validatePromo(@RequestBody @Valid PromoValidateRequest request, @AuthenticationPrincipal Jwt jwt) {
      BigDecimal total = request.orderTotal() != null ? request.orderTotal() : BigDecimal.ZERO;
      PromoValidateResponse response = this.promoCodeService.validate(request.code(), total);
      this.auditLogService.log(this.actorId(jwt), "VALIDATE_PROMO", "PromoCode", request.code(), "Validated promo code during checkout");
      return ResponseEntity.ok(response);
   }

   @Generated
   public CustomerController(final OrderService orderService, final PaymentService paymentService, final UserProfileService userProfileService, final RatingService ratingService, final PromoCodeService promoCodeService, final AuditLogService auditLogService) {
      this.orderService = orderService;
      this.paymentService = paymentService;
      this.userProfileService = userProfileService;
      this.ratingService = ratingService;
      this.promoCodeService = promoCodeService;
      this.auditLogService = auditLogService;
   }

   private String actorId(Jwt jwt) {
      return jwt != null && jwt.getClaimAsString("uuid") != null ? jwt.getClaimAsString("uuid") : jwt.getSubject();
   }
}
