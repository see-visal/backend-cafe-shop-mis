package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;

@Entity
@Table(
   name = "promo_codes"
)
public class PromoCode {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @Column(
      nullable = false,
      unique = true,
      length = 50
   )
   private String code;
   @Column(
      name = "discount_type",
      nullable = false,
      length = 20
   )
   private String discountType;
   @Column(
      name = "discount_value",
      nullable = false,
      precision = 10,
      scale = 2
   )
   private BigDecimal discountValue;
   @Column(
      name = "max_uses"
   )
   private Integer maxUses;
   @Column(
      name = "used_count",
      columnDefinition = "INT DEFAULT 0"
   )
   private Integer usedCount;
   @Column(
      name = "expires_at"
   )
   private LocalDateTime expiresAt;
   @Column(
      name = "active",
      columnDefinition = "BOOLEAN DEFAULT TRUE"
   )
   private Boolean active;
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
   private static Integer $default$usedCount() {
      return 0;
   }

   @Generated
   private static Boolean $default$active() {
      return true;
   }

   @Generated
   public static PromoCodeBuilder builder() {
      return new PromoCodeBuilder();
   }

   @Generated
   public UUID getId() {
      return this.id;
   }

   @Generated
   public String getCode() {
      return this.code;
   }

   @Generated
   public String getDiscountType() {
      return this.discountType;
   }

   @Generated
   public BigDecimal getDiscountValue() {
      return this.discountValue;
   }

   @Generated
   public Integer getMaxUses() {
      return this.maxUses;
   }

   @Generated
   public Integer getUsedCount() {
      return this.usedCount;
   }

   @Generated
   public LocalDateTime getExpiresAt() {
      return this.expiresAt;
   }

   @Generated
   public Boolean getActive() {
      return this.active;
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
   public void setCode(final String code) {
      this.code = code;
   }

   @Generated
   public void setDiscountType(final String discountType) {
      this.discountType = discountType;
   }

   @Generated
   public void setDiscountValue(final BigDecimal discountValue) {
      this.discountValue = discountValue;
   }

   @Generated
   public void setMaxUses(final Integer maxUses) {
      this.maxUses = maxUses;
   }

   @Generated
   public void setUsedCount(final Integer usedCount) {
      this.usedCount = usedCount;
   }

   @Generated
   public void setExpiresAt(final LocalDateTime expiresAt) {
      this.expiresAt = expiresAt;
   }

   @Generated
   public void setActive(final Boolean active) {
      this.active = active;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public PromoCode() {
      this.usedCount = $default$usedCount();
      this.active = $default$active();
   }

   @Generated
   public PromoCode(final UUID id, final String code, final String discountType, final BigDecimal discountValue, final Integer maxUses, final Integer usedCount, final LocalDateTime expiresAt, final Boolean active, final LocalDateTime createdAt) {
      this.id = id;
      this.code = code;
      this.discountType = discountType;
      this.discountValue = discountValue;
      this.maxUses = maxUses;
      this.usedCount = usedCount;
      this.expiresAt = expiresAt;
      this.active = active;
      this.createdAt = createdAt;
   }

   @Generated
   public static class PromoCodeBuilder {
      @Generated
      private UUID id;
      @Generated
      private String code;
      @Generated
      private String discountType;
      @Generated
      private BigDecimal discountValue;
      @Generated
      private Integer maxUses;
      @Generated
      private boolean usedCount$set;
      @Generated
      private Integer usedCount$value;
      @Generated
      private LocalDateTime expiresAt;
      @Generated
      private boolean active$set;
      @Generated
      private Boolean active$value;
      @Generated
      private LocalDateTime createdAt;

      @Generated
      PromoCodeBuilder() {
      }

      @Generated
      public PromoCodeBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public PromoCodeBuilder code(final String code) {
         this.code = code;
         return this;
      }

      @Generated
      public PromoCodeBuilder discountType(final String discountType) {
         this.discountType = discountType;
         return this;
      }

      @Generated
      public PromoCodeBuilder discountValue(final BigDecimal discountValue) {
         this.discountValue = discountValue;
         return this;
      }

      @Generated
      public PromoCodeBuilder maxUses(final Integer maxUses) {
         this.maxUses = maxUses;
         return this;
      }

      @Generated
      public PromoCodeBuilder usedCount(final Integer usedCount) {
         this.usedCount$value = usedCount;
         this.usedCount$set = true;
         return this;
      }

      @Generated
      public PromoCodeBuilder expiresAt(final LocalDateTime expiresAt) {
         this.expiresAt = expiresAt;
         return this;
      }

      @Generated
      public PromoCodeBuilder active(final Boolean active) {
         this.active$value = active;
         this.active$set = true;
         return this;
      }

      @Generated
      public PromoCodeBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public PromoCode build() {
         Integer usedCount$value = this.usedCount$value;
         if (!this.usedCount$set) {
            usedCount$value = PromoCode.$default$usedCount();
         }

         Boolean active$value = this.active$value;
         if (!this.active$set) {
            active$value = PromoCode.$default$active();
         }

         return new PromoCode(this.id, this.code, this.discountType, this.discountValue, this.maxUses, usedCount$value, this.expiresAt, active$value, this.createdAt);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "PromoCode.PromoCodeBuilder(id=" + var10000 + ", code=" + this.code + ", discountType=" + this.discountType + ", discountValue=" + String.valueOf(this.discountValue) + ", maxUses=" + this.maxUses + ", usedCount$value=" + this.usedCount$value + ", expiresAt=" + String.valueOf(this.expiresAt) + ", active$value=" + this.active$value + ", createdAt=" + String.valueOf(this.createdAt) + ")";
      }
   }
}
