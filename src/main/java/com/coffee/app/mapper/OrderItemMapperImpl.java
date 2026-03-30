package com.coffee.app.mapper;

import com.coffee.app.domain.OrderItem;
import com.coffee.app.domain.Product;
import com.coffee.app.dto.response.OrderItemResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapperImpl implements OrderItemMapper {
   public OrderItemResponse toResponse(OrderItem item) {
      if (item == null) {
         return null;
      } else {
         UUID productId = null;
         String productName = null;
         String productImageUrl = null;
         String specialInstructions = null;
         UUID id = null;
         int quantity = 0;
         BigDecimal unitPrice = null;
         productId = this.itemProductId(item);
         productName = this.itemProductName(item);
         productImageUrl = this.itemProductImageUrl(item);
         specialInstructions = item.getSpecialInstructions();
         id = item.getId();
         quantity = item.getQuantity();
         unitPrice = item.getUnitPrice();
         OrderItemResponse orderItemResponse = new OrderItemResponse(id, productId, productName, productImageUrl, quantity, unitPrice, specialInstructions);
         return orderItemResponse;
      }
   }

   private UUID itemProductId(OrderItem orderItem) {
      Product product = orderItem.getProduct();
      return product == null ? null : product.getId();
   }

   private String itemProductName(OrderItem orderItem) {
      Product product = orderItem.getProduct();
      return product == null ? null : product.getName();
   }

   private String itemProductImageUrl(OrderItem orderItem) {
      Product product = orderItem.getProduct();
      if (product == null || product.getImageUrl() == null || product.getImageUrl().isBlank()) {
         return null;
      }

      String value = product.getImageUrl().trim();
      if (value.startsWith("products/")) {
         return "/" + value;
      }

      return value;
   }
}
