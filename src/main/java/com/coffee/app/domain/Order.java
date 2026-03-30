package com.coffee.app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Generated;

@Entity
@Table(
   name = "orders"
)
public class Order {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @Column(
      name = "user_id"
   )
   private UUID userId;
   @Column(
      nullable = false,
      length = 30
   )
   private String status;
   @Column(
      name = "order_type",
      nullable = false,
      length = 20
   )
   private String orderType;
   @Column(
      name = "table_number"
   )
   private Integer tableNumber;
   @Column(
      name = "total_price",
      nullable = false,
      precision = 10,
      scale = 2
   )
   private BigDecimal totalPrice;
   @Column(
      name = "payment_ref",
      length = 100
   )
   private String paymentRef;
   @Column(
      name = "notes",
      length = 500
   )
   private String notes;
   @Column(
      name = "barista_id"
   )
   private UUID baristaId;
   @Column(
      name = "created_at",
      updatable = false
   )
   private LocalDateTime createdAt;
   @Column(
      name = "served_at"
   )
   private LocalDateTime servedAt;
   @Column(
      name = "pickup_token",
      length = 64
   )
   private String pickupToken;
   @Column(
      name = "pickup_token_expires_at"
   )
   private LocalDateTime pickupTokenExpiresAt;
   @Column(
      name = "discount_amount",
      precision = 10,
      scale = 2
   )
   private BigDecimal discountAmount;
   @Column(
      name = "promo_code",
      length = 50
   )
   private String promoCode;
   @Column(
      name = "loyalty_points_redeemed"
   )
   private Integer loyaltyPointsRedeemed;
   @OneToMany(
      mappedBy = "order",
      cascade = {CascadeType.ALL},
      orphanRemoval = true
   )
   private List<OrderItem> items;
   @Transient
   private String clientSecret;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }

   @Generated
   private static List<OrderItem> $default$items() {
      return new ArrayList<>();
   }

   @Generated
   public static OrderBuilder builder() {
      return new OrderBuilder();
   }

   @Generated
   public UUID getId() {
      return this.id;
   }

   @Generated
   public UUID getUserId() {
      return this.userId;
   }

   @Generated
   public String getStatus() {
      return this.status;
   }

   @Generated
   public String getOrderType() {
      return this.orderType;
   }

   @Generated
   public Integer getTableNumber() {
      return this.tableNumber;
   }

   @Generated
   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   @Generated
   public String getPaymentRef() {
      return this.paymentRef;
   }

   @Generated
   public String getNotes() {
      return this.notes;
   }

   @Generated
   public UUID getBaristaId() {
      return this.baristaId;
   }

   @Generated
   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   @Generated
   public LocalDateTime getServedAt() {
      return this.servedAt;
   }

   @Generated
   public String getPickupToken() {
      return this.pickupToken;
   }

   @Generated
   public LocalDateTime getPickupTokenExpiresAt() {
      return this.pickupTokenExpiresAt;
   }

   @Generated
   public BigDecimal getDiscountAmount() {
      return this.discountAmount;
   }

   @Generated
   public String getPromoCode() {
      return this.promoCode;
   }

   @Generated
   public Integer getLoyaltyPointsRedeemed() {
      return this.loyaltyPointsRedeemed;
   }

   @Generated
   public List<OrderItem> getItems() {
      return this.items;
   }

   @Generated
   public String getClientSecret() {
      return this.clientSecret;
   }

   @Generated
   public void setId(final UUID id) {
      this.id = id;
   }

   @Generated
   public void setUserId(final UUID userId) {
      this.userId = userId;
   }

   @Generated
   public void setStatus(final String status) {
      this.status = status;
   }

   @Generated
   public void setOrderType(final String orderType) {
      this.orderType = orderType;
   }

   @Generated
   public void setTableNumber(final Integer tableNumber) {
      this.tableNumber = tableNumber;
   }

   @Generated
   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   @Generated
   public void setPaymentRef(final String paymentRef) {
      this.paymentRef = paymentRef;
   }

   @Generated
   public void setNotes(final String notes) {
      this.notes = notes;
   }

   @Generated
   public void setBaristaId(final UUID baristaId) {
      this.baristaId = baristaId;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public void setServedAt(final LocalDateTime servedAt) {
      this.servedAt = servedAt;
   }

   @Generated
   public void setPickupToken(final String pickupToken) {
      this.pickupToken = pickupToken;
   }

   @Generated
   public void setPickupTokenExpiresAt(final LocalDateTime pickupTokenExpiresAt) {
      this.pickupTokenExpiresAt = pickupTokenExpiresAt;
   }

   @Generated
   public void setDiscountAmount(final BigDecimal discountAmount) {
      this.discountAmount = discountAmount;
   }

   @Generated
   public void setPromoCode(final String promoCode) {
      this.promoCode = promoCode;
   }

   @Generated
   public void setLoyaltyPointsRedeemed(final Integer loyaltyPointsRedeemed) {
      this.loyaltyPointsRedeemed = loyaltyPointsRedeemed;
   }

   @Generated
   public void setItems(final List<OrderItem> items) {
      this.items = items;
   }

   @Generated
   public void setClientSecret(final String clientSecret) {
      this.clientSecret = clientSecret;
   }

   @Generated
   public Order() {
      this.items = $default$items();
   }

   @Generated
   public Order(final UUID id, final UUID userId, final String status, final String orderType, final Integer tableNumber, final BigDecimal totalPrice, final String paymentRef, final String notes, final UUID baristaId, final LocalDateTime createdAt, final LocalDateTime servedAt, final String pickupToken, final LocalDateTime pickupTokenExpiresAt, final BigDecimal discountAmount, final String promoCode, final Integer loyaltyPointsRedeemed, final List<OrderItem> items, final String clientSecret) {
      this.id = id;
      this.userId = userId;
      this.status = status;
      this.orderType = orderType;
      this.tableNumber = tableNumber;
      this.totalPrice = totalPrice;
      this.paymentRef = paymentRef;
      this.notes = notes;
      this.baristaId = baristaId;
      this.createdAt = createdAt;
      this.servedAt = servedAt;
      this.pickupToken = pickupToken;
      this.pickupTokenExpiresAt = pickupTokenExpiresAt;
      this.discountAmount = discountAmount;
      this.promoCode = promoCode;
      this.loyaltyPointsRedeemed = loyaltyPointsRedeemed;
      this.items = items;
      this.clientSecret = clientSecret;
   }

   @Generated
   public static class OrderBuilder {
      @Generated
      private UUID id;
      @Generated
      private UUID userId;
      @Generated
      private String status;
      @Generated
      private String orderType;
      @Generated
      private Integer tableNumber;
      @Generated
      private BigDecimal totalPrice;
      @Generated
      private String paymentRef;
      @Generated
      private String notes;
      @Generated
      private UUID baristaId;
      @Generated
      private LocalDateTime createdAt;
      @Generated
      private LocalDateTime servedAt;
      @Generated
      private String pickupToken;
      @Generated
      private LocalDateTime pickupTokenExpiresAt;
      @Generated
      private BigDecimal discountAmount;
      @Generated
      private String promoCode;
      @Generated
      private Integer loyaltyPointsRedeemed;
      @Generated
      private boolean items$set;
      @Generated
      private List<OrderItem> items$value;
      @Generated
      private String clientSecret;

      @Generated
      OrderBuilder() {
      }

      @Generated
      public OrderBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public OrderBuilder userId(final UUID userId) {
         this.userId = userId;
         return this;
      }

      @Generated
      public OrderBuilder status(final String status) {
         this.status = status;
         return this;
      }

      @Generated
      public OrderBuilder orderType(final String orderType) {
         this.orderType = orderType;
         return this;
      }

      @Generated
      public OrderBuilder tableNumber(final Integer tableNumber) {
         this.tableNumber = tableNumber;
         return this;
      }

      @Generated
      public OrderBuilder totalPrice(final BigDecimal totalPrice) {
         this.totalPrice = totalPrice;
         return this;
      }

      @Generated
      public OrderBuilder paymentRef(final String paymentRef) {
         this.paymentRef = paymentRef;
         return this;
      }

      @Generated
      public OrderBuilder notes(final String notes) {
         this.notes = notes;
         return this;
      }

      @Generated
      public OrderBuilder baristaId(final UUID baristaId) {
         this.baristaId = baristaId;
         return this;
      }

      @Generated
      public OrderBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public OrderBuilder servedAt(final LocalDateTime servedAt) {
         this.servedAt = servedAt;
         return this;
      }

      @Generated
      public OrderBuilder pickupToken(final String pickupToken) {
         this.pickupToken = pickupToken;
         return this;
      }

      @Generated
      public OrderBuilder pickupTokenExpiresAt(final LocalDateTime pickupTokenExpiresAt) {
         this.pickupTokenExpiresAt = pickupTokenExpiresAt;
         return this;
      }

      @Generated
      public OrderBuilder discountAmount(final BigDecimal discountAmount) {
         this.discountAmount = discountAmount;
         return this;
      }

      @Generated
      public OrderBuilder promoCode(final String promoCode) {
         this.promoCode = promoCode;
         return this;
      }

      @Generated
      public OrderBuilder loyaltyPointsRedeemed(final Integer loyaltyPointsRedeemed) {
         this.loyaltyPointsRedeemed = loyaltyPointsRedeemed;
         return this;
      }

      @Generated
      public OrderBuilder items(final List<OrderItem> items) {
         this.items$value = items;
         this.items$set = true;
         return this;
      }

      @Generated
      public OrderBuilder clientSecret(final String clientSecret) {
         this.clientSecret = clientSecret;
         return this;
      }

      @Generated
      public Order build() {
         List<OrderItem> items$value = this.items$value;
         if (!this.items$set) {
            items$value = Order.$default$items();
         }

         return new Order(this.id, this.userId, this.status, this.orderType, this.tableNumber, this.totalPrice, this.paymentRef, this.notes, this.baristaId, this.createdAt, this.servedAt, this.pickupToken, this.pickupTokenExpiresAt, this.discountAmount, this.promoCode, this.loyaltyPointsRedeemed, items$value, this.clientSecret);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "Order.OrderBuilder(id=" + var10000 + ", userId=" + String.valueOf(this.userId) + ", status=" + this.status + ", orderType=" + this.orderType + ", tableNumber=" + this.tableNumber + ", totalPrice=" + String.valueOf(this.totalPrice) + ", paymentRef=" + this.paymentRef + ", notes=" + this.notes + ", baristaId=" + String.valueOf(this.baristaId) + ", createdAt=" + String.valueOf(this.createdAt) + ", servedAt=" + String.valueOf(this.servedAt) + ", pickupToken=" + this.pickupToken + ", pickupTokenExpiresAt=" + String.valueOf(this.pickupTokenExpiresAt) + ", discountAmount=" + String.valueOf(this.discountAmount) + ", promoCode=" + this.promoCode + ", loyaltyPointsRedeemed=" + this.loyaltyPointsRedeemed + ", items$value=" + String.valueOf(this.items$value) + ", clientSecret=" + this.clientSecret + ")";
      }
   }
}
