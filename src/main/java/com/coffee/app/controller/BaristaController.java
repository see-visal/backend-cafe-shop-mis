package com.coffee.app.controller;

import com.coffee.app.dto.request.StatusUpdateRequest;
import com.coffee.app.dto.request.StockAdjustRequest;
import com.coffee.app.dto.response.IngredientResponse;
import com.coffee.app.dto.response.OrderResponse;
import com.coffee.app.dto.response.ProductResponse;
import com.coffee.app.service.AuditLogService;
import com.coffee.app.service.InventoryService;
import com.coffee.app.service.OrderService;
import com.coffee.app.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/barista"})
@PreAuthorize("hasAnyRole('BARISTA', 'ADMIN')")
public class BaristaController {
   private final OrderService orderService;
   private final InventoryService inventoryService;
   private final ProductService productService;
   private final AuditLogService auditLogService;

   @GetMapping({"/queue"})
   public ResponseEntity<List<OrderResponse>> getQueue() {
      return ResponseEntity.ok(this.orderService.getBaristaQueue());
   }

   @PutMapping({"/orders/{id}/status"})
   public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID id, @RequestBody @Valid StatusUpdateRequest request, @AuthenticationPrincipal Jwt jwt) {
      UUID baristaId = jwt != null ? UUID.fromString(jwt.getClaimAsString("uuid")) : null;
      OrderResponse order = this.orderService.updateStatus(id, request.status(), baristaId);
      this.auditLogService.log(this.actorId(jwt), "BARISTA_UPDATE_ORDER_STATUS", "Order", id.toString(), "Set order status to " + request.status());
      return ResponseEntity.ok(order);
   }

   @PostMapping({"/orders/{id}/pickup"})
   public ResponseEntity<OrderResponse> confirmPickup(@PathVariable UUID id, @RequestBody Map<String, String> body, @AuthenticationPrincipal Jwt jwt) {
      String token = (String)body.get("token");
      UUID baristaId = jwt != null ? UUID.fromString(jwt.getClaimAsString("uuid")) : null;
      OrderResponse order = this.orderService.confirmPickup(id, token, baristaId);
      this.auditLogService.log(this.actorId(jwt), "CONFIRM_PICKUP", "Order", id.toString(), "Confirmed customer pickup");
      return ResponseEntity.ok(order);
   }

   @GetMapping({"/stock-alerts"})
   public ResponseEntity<List<IngredientResponse>> stockAlerts() {
      return ResponseEntity.ok(this.inventoryService.getLowStockIngredients());
   }

   @PatchMapping({"/ingredients/adjust-stock"})
   public ResponseEntity<IngredientResponse> adjustStock(@RequestBody @Valid StockAdjustRequest request, @AuthenticationPrincipal Jwt jwt) {
      IngredientResponse ingredient = this.inventoryService.adjustStock(request);
      this.auditLogService.log(this.actorId(jwt), "ADJUST_STOCK", "Ingredient", ingredient.id().toString(), "Adjusted stock by " + request.delta());
      return ResponseEntity.ok(ingredient);
   }

   @PatchMapping({"/products/{id}/availability"})
   public ResponseEntity<ProductResponse> toggleAvailability(@PathVariable UUID id, @RequestParam boolean available, @AuthenticationPrincipal Jwt jwt) {
      ProductResponse product = this.productService.setAvailability(id, available);
      this.auditLogService.log(this.actorId(jwt), available ? "MARK_PRODUCT_AVAILABLE" : "MARK_PRODUCT_UNAVAILABLE", "Product", id.toString(), "Set product availability to " + available);
      return ResponseEntity.ok(product);
   }

   @Generated
   public BaristaController(final OrderService orderService, final InventoryService inventoryService, final ProductService productService, final AuditLogService auditLogService) {
      this.orderService = orderService;
      this.inventoryService = inventoryService;
      this.productService = productService;
      this.auditLogService = auditLogService;
   }

   private String actorId(Jwt jwt) {
      return jwt != null && jwt.getClaimAsString("uuid") != null ? jwt.getClaimAsString("uuid") : "BARISTA";
   }
}
