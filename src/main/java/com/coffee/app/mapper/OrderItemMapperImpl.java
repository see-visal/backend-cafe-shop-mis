package com.coffee.app.mapper;

import com.coffee.app.domain.OrderItem;
import com.coffee.app.domain.Product;
import com.coffee.app.dto.response.OrderItemResponse;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapperImpl implements OrderItemMapper {
   private static final Set<String> LOCAL_PRODUCT_ASSETS = Set.of(
      "products/espresso.jpg",
      "products/cappuccino.jpg",
      "products/iced_latte.jpg",
      "products/matcha_green_tea.jpg",
      "products/croissant.jpg"
   );

   @Value("${minio.bucket-name:coffeeshop-files}")
   private String bucketName;

   @Value("${minio.public-endpoint:http://localhost:9000}")
   private String minioPublicEndpoint;

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
         if (LOCAL_PRODUCT_ASSETS.contains(value)) {
            return "/" + value;
         }

         return sanitizeMinioBase(this.minioPublicEndpoint) + "/" + this.bucketName + "/" + value;
      }

      return value;
   }

   private String sanitizeMinioBase(String endpoint) {
      if (endpoint == null || endpoint.isBlank()) {
         return "http://localhost:9000";
      }

      String trimmed = endpoint.trim().replaceAll("/+$", "");
      return trimmed.replaceFirst("/browser/?$", "");
   }
}
