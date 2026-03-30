package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(
   name = "order_items"
)
public class OrderItem {
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
      nullable = false
   )
   private int quantity;
   @Column(
      name = "unit_price",
      nullable = false,
      precision = 10,
      scale = 2
   )
   private BigDecimal unitPrice;
   @JdbcTypeCode(3001)
   @Column(
      columnDefinition = "jsonb"
   )
   private Map<String, Object> modifiers;
   @Column(
      name = "special_instructions",
      length = 500
   )
   private String specialInstructions;

   @Generated
   public static OrderItemBuilder builder() {
      return new OrderItemBuilder();
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
   public int getQuantity() {
      return this.quantity;
   }

   @Generated
   public BigDecimal getUnitPrice() {
      return this.unitPrice;
   }

   @Generated
   public Map<String, Object> getModifiers() {
      return this.modifiers;
   }

   @Generated
   public String getSpecialInstructions() {
      return this.specialInstructions;
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
   public void setQuantity(final int quantity) {
      this.quantity = quantity;
   }

   @Generated
   public void setUnitPrice(final BigDecimal unitPrice) {
      this.unitPrice = unitPrice;
   }

   @Generated
   public void setModifiers(final Map<String, Object> modifiers) {
      this.modifiers = modifiers;
   }

   @Generated
   public void setSpecialInstructions(final String specialInstructions) {
      this.specialInstructions = specialInstructions;
   }

   @Generated
   public OrderItem() {
   }

   @Generated
   public OrderItem(final UUID id, final Order order, final Product product, final int quantity, final BigDecimal unitPrice, final Map<String, Object> modifiers, final String specialInstructions) {
      this.id = id;
      this.order = order;
      this.product = product;
      this.quantity = quantity;
      this.unitPrice = unitPrice;
      this.modifiers = modifiers;
      this.specialInstructions = specialInstructions;
   }

   @Generated
   public static class OrderItemBuilder {
      @Generated
      private UUID id;
      @Generated
      private Order order;
      @Generated
      private Product product;
      @Generated
      private int quantity;
      @Generated
      private BigDecimal unitPrice;
      @Generated
      private Map<String, Object> modifiers;
      @Generated
      private String specialInstructions;

      @Generated
      OrderItemBuilder() {
      }

      @Generated
      public OrderItemBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public OrderItemBuilder order(final Order order) {
         this.order = order;
         return this;
      }

      @Generated
      public OrderItemBuilder product(final Product product) {
         this.product = product;
         return this;
      }

      @Generated
      public OrderItemBuilder quantity(final int quantity) {
         this.quantity = quantity;
         return this;
      }

      @Generated
      public OrderItemBuilder unitPrice(final BigDecimal unitPrice) {
         this.unitPrice = unitPrice;
         return this;
      }

      @Generated
      public OrderItemBuilder modifiers(final Map<String, Object> modifiers) {
         this.modifiers = modifiers;
         return this;
      }

      @Generated
      public OrderItemBuilder specialInstructions(final String specialInstructions) {
         this.specialInstructions = specialInstructions;
         return this;
      }

      @Generated
      public OrderItem build() {
         return new OrderItem(this.id, this.order, this.product, this.quantity, this.unitPrice, this.modifiers, this.specialInstructions);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "OrderItem.OrderItemBuilder(id=" + var10000 + ", order=" + String.valueOf(this.order) + ", product=" + String.valueOf(this.product) + ", quantity=" + this.quantity + ", unitPrice=" + String.valueOf(this.unitPrice) + ", modifiers=" + String.valueOf(this.modifiers) + ", specialInstructions=" + this.specialInstructions + ")";
      }
   }
}
