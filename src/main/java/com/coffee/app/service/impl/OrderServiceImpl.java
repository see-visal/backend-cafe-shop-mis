package com.coffee.app.service.impl;

import com.coffee.app.controller.CustomerController;
import com.coffee.app.domain.Order;
import com.coffee.app.domain.OrderItem;
import com.coffee.app.domain.Product;
import com.coffee.app.dto.request.OrderItemRequest;
import com.coffee.app.dto.request.OrderRequest;
import com.coffee.app.dto.response.OrderResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.OrderMapper;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.repository.PaymentRepository;
import com.coffee.app.repository.ProductRepository;
import com.coffee.app.repository.PromoCodeRepository;
import com.coffee.app.service.NotificationService;
import com.coffee.app.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
   private static final String STRIPE_PLACEHOLDER_KEY = "sk_test_123";
   private final OrderRepository orderRepository;
   private final ProductRepository productRepository;
   private final PaymentRepository paymentRepository;
   private final PromoCodeRepository promoCodeRepository;
   private final OrderMapper orderMapper;
   private final NotificationService notificationService;
   @Value("${stripe.api-key:}")
   private String stripeApiKey;

   @Transactional
   public OrderResponse placeOrder(OrderRequest request) {
      Order order = Order.builder().userId(request.userId()).orderType(request.orderType()).tableNumber(request.tableNumber()).notes(request.notes()).status("PENDING_PAYMENT").totalPrice(BigDecimal.ZERO).build();
      BigDecimal total = BigDecimal.ZERO;

      for(OrderItemRequest itemReq : request.items()) {
         Product product = this.productRepository.findById(itemReq.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + String.valueOf(itemReq.productId())));
         if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available: " + product.getName());
         }

         OrderItem item = OrderItem.builder().order(order).product(product).quantity(itemReq.quantity()).unitPrice(itemReq.unitPrice()).specialInstructions(itemReq.specialInstructions()).build();
         order.getItems().add(item);
         total = total.add(itemReq.unitPrice().multiply(BigDecimal.valueOf((long)itemReq.quantity())));
      }

      // Apply promo discount if provided
      BigDecimal discount = BigDecimal.ZERO;
      if (request.promoCode() != null && !request.promoCode().isBlank()) {
         BigDecimal requestedDiscount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
         // Cap the discount so the total never goes negative
         discount = requestedDiscount.min(total);
         order.setPromoCode(request.promoCode().toUpperCase().trim());
         order.setDiscountAmount(discount);
         // Increment usedCount on the promo code (best-effort: ignore if not found)
         this.promoCodeRepository.findByCodeIgnoreCase(request.promoCode()).ifPresent(promo -> {
            promo.setUsedCount((promo.getUsedCount() != null ? promo.getUsedCount() : 0) + 1);
            this.promoCodeRepository.save(promo);
         });
      }

      BigDecimal finalTotal = total.subtract(discount).max(BigDecimal.ZERO);
      order.setTotalPrice(finalTotal);
      order = this.orderRepository.save(order);

      // Create notification for new order
      String itemSummary = order.getItems().stream()
         .map(item -> item.getQuantity() + "× " + item.getProduct().getName())
         .reduce((a, b) -> a + ", " + b)
         .orElse("Unknown items");
      
      String tableInfo = order.getTableNumber() != null ? " — Table " + order.getTableNumber() : "";
      this.notificationService.createNotification(
         "order",
         "New Order #" + order.getId().toString().substring(0, 8).toUpperCase() + tableInfo,
         itemSummary + ". Total: $" + finalTotal + ". Status: Pending Payment.",
         "medium",
         UUID.fromString("00000000-0000-0000-0000-000000000001"), // Default admin ID - will be updated
         order.getId()
      );

      boolean stripeConfigured = this.stripeApiKey != null && !this.stripeApiKey.isBlank() && !"sk_test_123".equals(this.stripeApiKey);
      if (finalTotal.compareTo(BigDecimal.ZERO) > 0 && stripeConfigured) {
         try {
            long amountInCents = finalTotal.multiply(BigDecimal.valueOf(100L)).longValue();
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(amountInCents).setCurrency("usd").putMetadata("order_id", order.getId().toString()).build();
            PaymentIntent intent = PaymentIntent.create(params);
            order.setPaymentRef(intent.getId());
            order.setClientSecret(intent.getClientSecret());
            order = this.orderRepository.save(order);
         } catch (StripeException e) {
            throw new RuntimeException("Failed to initialize payment: " + e.getMessage());
         }
      }

      return this.orderMapper.toResponse(order);
   }

   @Transactional(
      readOnly = true
   )
   public OrderResponse getOrder(UUID id) {
      return this.orderMapper.toResponse(this.findOrThrow(id));
   }

   @Transactional(
      readOnly = true
   )
   public Page<OrderResponse> getAllOrders(Pageable pageable, String status, LocalDateTime from, LocalDateTime to) {
      String s = status != null && !status.isBlank() ? status.toUpperCase() : null;
      return this.orderRepository.findWithFilters(s, from, to, pageable).map(this.orderMapper::toResponse);
   }

   @Transactional(
      readOnly = true
   )
   public List<OrderResponse> getOrdersByUser(UUID userId) {
      return this.orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this.orderMapper::toResponse).toList();
   }

   @Transactional(
      readOnly = true
   )
   public Page<OrderResponse> getOrdersByUserPaginated(UUID userId, String status, Pageable pageable) {
      String statusFilter = status != null && !status.isBlank() ? status.toUpperCase() : null;
      return this.orderRepository.findByUserIdAndOptionalStatus(userId, statusFilter, pageable).map(this.orderMapper::toResponse);
   }

   @Transactional(
      readOnly = true
   )
   public List<OrderResponse> getBaristaQueue() {
      return this.orderRepository.findBaristaQueue().stream().map(this.orderMapper::toResponse).toList();
   }

   @Transactional
   public OrderResponse updateStatus(UUID id, String status, UUID baristaId) {
      Order order = this.findOrThrow(id);
      if ("PENDING_PAYMENT".equals(order.getStatus()) && "CONFIRMED".equals(status)) {
         this.paymentRepository.findByOrderId(order.getId()).ifPresent((payment) -> {
            payment.setStatus("PAID");
            payment.setPaidAt(LocalDateTime.now());
            this.paymentRepository.save(payment);
         });
      }

      if ("READY".equals(status) && !"READY".equals(order.getStatus())) {
         order.setPickupToken(UUID.randomUUID().toString());
         order.setPickupTokenExpiresAt(LocalDateTime.now().plusMinutes(60L));
      }

      order.setStatus(status);
      if (baristaId != null) {
         order.setBaristaId(baristaId);
      }

      if ("SERVED".equals(status)) {
         order.setServedAt(LocalDateTime.now());
         order.setPickupToken((String)null);
         order.setPickupTokenExpiresAt((LocalDateTime)null);
      }

      Order saved = this.orderRepository.save(order);
      
      // Create notification for status update
      String statusMessage = switch(status) {
         case "CONFIRMED" -> "Payment confirmed and order moved to preparation.";
         case "PREPARING" -> "Barista has started preparing the order.";
         case "READY" -> "Order is ready for pickup!";
         case "SERVED" -> "Order has been served to the customer.";
         case "CANCELLED" -> "Order has been cancelled.";
         default -> "Status updated to: " + status;
      };
      
      this.notificationService.createNotification(
         "order",
         "Order #" + order.getId().toString().substring(0, 8).toUpperCase() + " — " + status,
         statusMessage,
         "low",
         UUID.fromString("00000000-0000-0000-0000-000000000001"),
         order.getId()
      );
      
      OrderResponse response = this.orderMapper.toResponse(saved);
      CustomerController.pushOrderUpdate(saved.getId().toString(), response);
      return response;
   }

   @Transactional
   public void cancelOrder(UUID id) {
      Order order = this.findOrThrow(id);
      if ("SERVED".equals(order.getStatus())) {
         throw new IllegalArgumentException("Cannot cancel an order that has already been served.");
      } else {
         order.setStatus("CANCELLED");
         Order saved = this.orderRepository.save(order);
         CustomerController.pushOrderUpdate(saved.getId().toString(), this.orderMapper.toResponse(saved));
      }
   }

   @Transactional
   public OrderResponse confirmPickup(UUID orderId, String token, UUID baristaId) {
      Order order = this.findOrThrow(orderId);
      if (!"READY".equals(order.getStatus())) {
         throw new IllegalArgumentException("Order is not in READY status. Current: " + order.getStatus());
      } else if (token != null && token.equals(order.getPickupToken())) {
         if (order.getPickupTokenExpiresAt() != null && order.getPickupTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Pickup token has expired.");
         } else {
            order.setStatus("SERVED");
            order.setServedAt(LocalDateTime.now());
            order.setPickupToken((String)null);
            order.setPickupTokenExpiresAt((LocalDateTime)null);
            if (baristaId != null) {
               order.setBaristaId(baristaId);
            }

            Order saved = this.orderRepository.save(order);
            OrderResponse response = this.orderMapper.toResponse(saved);
            CustomerController.pushOrderUpdate(saved.getId().toString(), response);
            return response;
         }
      } else {
         throw new IllegalArgumentException("Invalid pickup token.");
      }
   }

   private Order findOrThrow(UUID id) {
      return this.orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + String.valueOf(id)));
   }
}
