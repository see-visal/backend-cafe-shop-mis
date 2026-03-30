package com.coffee.app.mapper;

import com.coffee.app.domain.Product;
import com.coffee.app.dto.request.ProductRequest;
import com.coffee.app.dto.response.ProductResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {
   @Value("${minio.bucket-name:coffeeshop-files}")
   private String bucketName;

   @Value("${minio.public-endpoint:http://localhost:9000}")
   private String minioPublicEndpoint;

   public ProductResponse toResponse(Product product) {
      if (product == null) {
         return null;
      } else {
         UUID id = product.getId();
         String name = product.getName();
         String description = product.getDescription();
         BigDecimal price = product.getPrice();
         String imageUrl = this.normalizeImageUrl(product.getImageUrl());
         boolean active = product.isActive();
         String category = product.getCategory() != null ? product.getCategory().getName() : null;
         boolean showOnHomepage = product.isShowOnHomepage();
         boolean todaySpecial = product.isTodaySpecial();
         Integer homePriority = product.getHomePriority();
         return new ProductResponse(id, name, description, price, category, imageUrl, active, showOnHomepage, todaySpecial, homePriority);
      }
   }

   public Product toEntity(ProductRequest request) {
      if (request == null) {
         return null;
      } else {
         Product.ProductBuilder product = Product.builder();
         product.name(request.name());
         product.description(request.description());
         product.price(request.price());
         product.imageUrl(request.imageUrl());
         product.showOnHomepage(Boolean.TRUE.equals(request.showOnHomepage()));
         product.todaySpecial(Boolean.TRUE.equals(request.todaySpecial()));
         product.homePriority(request.homePriority());
         product.active(true);
         return product.build();
      }
   }

   public void updateEntity(ProductRequest request, Product product) {
      if (request != null) {
         product.setName(request.name());
         product.setDescription(request.description());
         product.setPrice(request.price());
         product.setImageUrl(request.imageUrl());
         product.setShowOnHomepage(Boolean.TRUE.equals(request.showOnHomepage()));
         product.setTodaySpecial(Boolean.TRUE.equals(request.todaySpecial()));
         product.setHomePriority(request.homePriority());
      }
   }

   private String normalizeImageUrl(String imageUrl) {
      if (imageUrl == null || imageUrl.isBlank()) {
         return imageUrl;
      }

      String value = imageUrl.trim();
      if (value.startsWith("http://") || value.startsWith("https://")) {
         return value;
      }

      String base = this.minioPublicEndpoint.endsWith("/")
         ? this.minioPublicEndpoint.substring(0, this.minioPublicEndpoint.length() - 1)
         : this.minioPublicEndpoint;
      String normalized = value.startsWith("/") ? value.substring(1) : value;

      if (normalized.startsWith(this.bucketName + "/")) {
         return base + "/" + normalized;
      }

      if (normalized.startsWith("products/") || value.startsWith("/products/")) {
         return "/" + normalized;
      }

      return value;
   }
}
