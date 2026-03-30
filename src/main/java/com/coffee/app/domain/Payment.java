package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;

@Entity
@Table(
   name = "payments"
)
public class Payment {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @OneToOne(
      fetch = FetchType.LAZY
   )
   @JoinColumn(
      name = "order_id",
      nullable = false,
      unique = true
   )
   private Order order;
   @Column(
      name = "payment_method",
      nullable = false,
      length = 20
   )
   private String paymentMethod;
   @Column(
      nullable = false,
      length = 20
   )
   private String status;
   @Column(
      nullable = false,
      precision = 10,
      scale = 2
   )
   private BigDecimal amount;
   @Column(
      name = "transaction_ref",
      length = 100
   )
   private String transactionRef;
   @Column(
      name = "paid_at"
   )
   private LocalDateTime paidAt;
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
   private static String $default$status() {
      return "PENDING";
   }

   @Generated
   public static PaymentBuilder builder() {
      return new PaymentBuilder();
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
   public String getPaymentMethod() {
      return this.paymentMethod;
   }

   @Generated
   public String getStatus() {
      return this.status;
   }

   @Generated
   public BigDecimal getAmount() {
      return this.amount;
   }

   @Generated
   public String getTransactionRef() {
      return this.transactionRef;
   }

   @Generated
   public LocalDateTime getPaidAt() {
      return this.paidAt;
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
   public void setPaymentMethod(final String paymentMethod) {
      this.paymentMethod = paymentMethod;
   }

   @Generated
   public void setStatus(final String status) {
      this.status = status;
   }

   @Generated
   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   @Generated
   public void setTransactionRef(final String transactionRef) {
      this.transactionRef = transactionRef;
   }

   @Generated
   public void setPaidAt(final LocalDateTime paidAt) {
      this.paidAt = paidAt;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public Payment() {
      this.status = $default$status();
   }

   @Generated
   public Payment(final UUID id, final Order order, final String paymentMethod, final String status, final BigDecimal amount, final String transactionRef, final LocalDateTime paidAt, final LocalDateTime createdAt) {
      this.id = id;
      this.order = order;
      this.paymentMethod = paymentMethod;
      this.status = status;
      this.amount = amount;
      this.transactionRef = transactionRef;
      this.paidAt = paidAt;
      this.createdAt = createdAt;
   }

   @Generated
   public static class PaymentBuilder {
      @Generated
      private UUID id;
      @Generated
      private Order order;
      @Generated
      private String paymentMethod;
      @Generated
      private boolean status$set;
      @Generated
      private String status$value;
      @Generated
      private BigDecimal amount;
      @Generated
      private String transactionRef;
      @Generated
      private LocalDateTime paidAt;
      @Generated
      private LocalDateTime createdAt;

      @Generated
      PaymentBuilder() {
      }

      @Generated
      public PaymentBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public PaymentBuilder order(final Order order) {
         this.order = order;
         return this;
      }

      @Generated
      public PaymentBuilder paymentMethod(final String paymentMethod) {
         this.paymentMethod = paymentMethod;
         return this;
      }

      @Generated
      public PaymentBuilder status(final String status) {
         this.status$value = status;
         this.status$set = true;
         return this;
      }

      @Generated
      public PaymentBuilder amount(final BigDecimal amount) {
         this.amount = amount;
         return this;
      }

      @Generated
      public PaymentBuilder transactionRef(final String transactionRef) {
         this.transactionRef = transactionRef;
         return this;
      }

      @Generated
      public PaymentBuilder paidAt(final LocalDateTime paidAt) {
         this.paidAt = paidAt;
         return this;
      }

      @Generated
      public PaymentBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public Payment build() {
         String status$value = this.status$value;
         if (!this.status$set) {
            status$value = Payment.$default$status();
         }

         return new Payment(this.id, this.order, this.paymentMethod, status$value, this.amount, this.transactionRef, this.paidAt, this.createdAt);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "Payment.PaymentBuilder(id=" + var10000 + ", order=" + String.valueOf(this.order) + ", paymentMethod=" + this.paymentMethod + ", status$value=" + this.status$value + ", amount=" + String.valueOf(this.amount) + ", transactionRef=" + this.transactionRef + ", paidAt=" + String.valueOf(this.paidAt) + ", createdAt=" + String.valueOf(this.createdAt) + ")";
      }
   }
}
