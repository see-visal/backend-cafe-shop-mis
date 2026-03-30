package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Generated;

@Entity
@Table(
   name = "ingredients"
)
public class Ingredient {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @Column(
      nullable = false,
      unique = true,
      length = 100
   )
   private String name;
   @Column(
      nullable = false,
      length = 20
   )
   private String unit;
   @Column(
      name = "stock_qty",
      nullable = false,
      precision = 10,
      scale = 3
   )
   private BigDecimal stockQty;
   @Column(
      name = "low_threshold",
      nullable = false,
      precision = 10,
      scale = 3
   )
   private BigDecimal lowThreshold;
   @Column(
      length = 150
   )
   private String supplier;
   @Column(
      length = 512
   )
   private String imageUrl;

   @Generated
   private static BigDecimal $default$stockQty() {
      return BigDecimal.ZERO;
   }

   @Generated
   private static BigDecimal $default$lowThreshold() {
      return BigDecimal.ZERO;
   }

   @Generated
   public static IngredientBuilder builder() {
      return new IngredientBuilder();
   }

   @Generated
   public UUID getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getUnit() {
      return this.unit;
   }

   @Generated
   public BigDecimal getStockQty() {
      return this.stockQty;
   }

   @Generated
   public BigDecimal getLowThreshold() {
      return this.lowThreshold;
   }

   @Generated
   public String getSupplier() {
      return this.supplier;
   }

   @Generated
   public String getImageUrl() {
      return this.imageUrl;
   }

   @Generated
   public void setId(final UUID id) {
      this.id = id;
   }

   @Generated
   public void setName(final String name) {
      this.name = name;
   }

   @Generated
   public void setUnit(final String unit) {
      this.unit = unit;
   }

   @Generated
   public void setStockQty(final BigDecimal stockQty) {
      this.stockQty = stockQty;
   }

   @Generated
   public void setLowThreshold(final BigDecimal lowThreshold) {
      this.lowThreshold = lowThreshold;
   }

   @Generated
   public void setSupplier(final String supplier) {
      this.supplier = supplier;
   }

   @Generated
   public void setImageUrl(final String imageUrl) {
      this.imageUrl = imageUrl;
   }

   @Generated
   public Ingredient() {
      this.stockQty = $default$stockQty();
      this.lowThreshold = $default$lowThreshold();
   }

   @Generated
   public Ingredient(final UUID id, final String name, final String unit, final BigDecimal stockQty, final BigDecimal lowThreshold, final String supplier, final String imageUrl) {
      this.id = id;
      this.name = name;
      this.unit = unit;
      this.stockQty = stockQty;
      this.lowThreshold = lowThreshold;
      this.supplier = supplier;
      this.imageUrl = imageUrl;
   }

   @Generated
   public static class IngredientBuilder {
      @Generated
      private UUID id;
      @Generated
      private String name;
      @Generated
      private String unit;
      @Generated
      private boolean stockQty$set;
      @Generated
      private BigDecimal stockQty$value;
      @Generated
      private boolean lowThreshold$set;
      @Generated
      private BigDecimal lowThreshold$value;
      @Generated
      private String supplier;
      @Generated
      private String imageUrl;

      @Generated
      IngredientBuilder() {
      }

      @Generated
      public IngredientBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public IngredientBuilder name(final String name) {
         this.name = name;
         return this;
      }

      @Generated
      public IngredientBuilder unit(final String unit) {
         this.unit = unit;
         return this;
      }

      @Generated
      public IngredientBuilder stockQty(final BigDecimal stockQty) {
         this.stockQty$value = stockQty;
         this.stockQty$set = true;
         return this;
      }

      @Generated
      public IngredientBuilder lowThreshold(final BigDecimal lowThreshold) {
         this.lowThreshold$value = lowThreshold;
         this.lowThreshold$set = true;
         return this;
      }

      @Generated
      public IngredientBuilder supplier(final String supplier) {
         this.supplier = supplier;
         return this;
      }

      @Generated
      public IngredientBuilder imageUrl(final String imageUrl) {
         this.imageUrl = imageUrl;
         return this;
      }

      @Generated
      public Ingredient build() {
         BigDecimal stockQty$value = this.stockQty$value;
         if (!this.stockQty$set) {
            stockQty$value = Ingredient.$default$stockQty();
         }

         BigDecimal lowThreshold$value = this.lowThreshold$value;
         if (!this.lowThreshold$set) {
            lowThreshold$value = Ingredient.$default$lowThreshold();
         }

         return new Ingredient(this.id, this.name, this.unit, stockQty$value, lowThreshold$value, this.supplier, this.imageUrl);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "Ingredient.IngredientBuilder(id=" + var10000 + ", name=" + this.name + ", unit=" + this.unit + ", stockQty$value=" + String.valueOf(this.stockQty$value) + ", lowThreshold$value=" + String.valueOf(this.lowThreshold$value) + ", supplier=" + this.supplier + ", imageUrl=" + this.imageUrl + ")";
      }
   }
}
