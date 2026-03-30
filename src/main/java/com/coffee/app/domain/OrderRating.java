package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;

@Entity
@Table(
   name = "order_ratings"
)
public class OrderRating {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @ManyToOne(
      fetch = FetchType.LAZY
   )
   @JoinColumn(
      name = "order_id",
      nullable = false
   )
   private Order order;
   @ManyToOne(
      fetch = FetchType.LAZY
   )
   @JoinColumn(
      name = "product_id",
      nullable = false
   )
   private Product product;
   @Column(
      name = "user_id",
      nullable = false
   )
   private UUID userId;
   @Column(
      name = "stars",
      nullable = false
   )
   private Integer stars;
   @Column(
      name = "comment",
      length = 500
   )
   private String comment;
   @Column(
      name = "created_at",
      updatable = false
   )
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }

   @Generated
   public static OrderRatingBuilder builder() {
      return new OrderRatingBuilder();
   }

   @Generated
   public UUID getId() {
      return this.id;
   }

   @Generated
   public Order getOrder() {
      return this.order;
   }

   @Generated
   public Product getProduct() {
      return this.product;
   }

   @Generated
   public UUID getUserId() {
      return this.userId;
   }

   @Generated
   public Integer getStars() {
      return this.stars;
   }

   @Generated
   public String getComment() {
      return this.comment;
   }

   @Generated
   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   @Generated
   public void setId(final UUID id) {
      this.id = id;
   }

   @Generated
   public void setOrder(final Order order) {
      this.order = order;
   }

   @Generated
   public void setProduct(final Product product) {
      this.product = product;
   }

   @Generated
   public void setUserId(final UUID userId) {
      this.userId = userId;
   }

   @Generated
   public void setStars(final Integer stars) {
      this.stars = stars;
   }

   @Generated
   public void setComment(final String comment) {
      this.comment = comment;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public OrderRating() {
   }

   @Generated
   public OrderRating(final UUID id, final Order order, final Product product, final UUID userId, final Integer stars, final String comment, final LocalDateTime createdAt) {
      this.id = id;
      this.order = order;
      this.product = product;
      this.userId = userId;
      this.stars = stars;
      this.comment = comment;
      this.createdAt = createdAt;
   }

   @Generated
   public static class OrderRatingBuilder {
      @Generated
      private UUID id;
      @Generated
      private Order order;
      @Generated
      private Product product;
      @Generated
      private UUID userId;
      @Generated
      private Integer stars;
      @Generated
      private String comment;
      @Generated
      private LocalDateTime createdAt;

      @Generated
      OrderRatingBuilder() {
      }

      @Generated
      public OrderRatingBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public OrderRatingBuilder order(final Order order) {
         this.order = order;
         return this;
      }

      @Generated
      public OrderRatingBuilder product(final Product product) {
         this.product = product;
         return this;
      }

      @Generated
      public OrderRatingBuilder userId(final UUID userId) {
         this.userId = userId;
         return this;
      }

      @Generated
      public OrderRatingBuilder stars(final Integer stars) {
         this.stars = stars;
         return this;
      }

      @Generated
      public OrderRatingBuilder comment(final String comment) {
         this.comment = comment;
         return this;
      }

      @Generated
      public OrderRatingBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public OrderRating build() {
         return new OrderRating(this.id, this.order, this.product, this.userId, this.stars, this.comment, this.createdAt);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "OrderRating.OrderRatingBuilder(id=" + var10000 + ", order=" + String.valueOf(this.order) + ", product=" + String.valueOf(this.product) + ", userId=" + String.valueOf(this.userId) + ", stars=" + this.stars + ", comment=" + this.comment + ", createdAt=" + String.valueOf(this.createdAt) + ")";
      }
   }
}
