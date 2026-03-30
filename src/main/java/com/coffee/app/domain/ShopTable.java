package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Generated;

@Entity
@Table(
   name = "shop_tables"
)
public class ShopTable {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "table_number",
      nullable = false,
      unique = true
   )
   private Integer tableNumber;
   @Column(
      name = "label",
      length = 50
   )
   private String label;
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
   private static Boolean $default$active() {
      return true;
   }

   @Generated
   public static ShopTableBuilder builder() {
      return new ShopTableBuilder();
   }

   @Generated
   public Long getId() {
      return this.id;
   }

   @Generated
   public Integer getTableNumber() {
      return this.tableNumber;
   }

   @Generated
   public String getLabel() {
      return this.label;
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
   public void setId(final Long id) {
      this.id = id;
   }

   @Generated
   public void setTableNumber(final Integer tableNumber) {
      this.tableNumber = tableNumber;
   }

   @Generated
   public void setLabel(final String label) {
      this.label = label;
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
   public ShopTable() {
      this.active = $default$active();
   }

   @Generated
   public ShopTable(final Long id, final Integer tableNumber, final String label, final Boolean active, final LocalDateTime createdAt) {
      this.id = id;
      this.tableNumber = tableNumber;
      this.label = label;
      this.active = active;
      this.createdAt = createdAt;
   }

   @Generated
   public static class ShopTableBuilder {
      @Generated
      private Long id;
      @Generated
      private Integer tableNumber;
      @Generated
      private String label;
      @Generated
      private boolean active$set;
      @Generated
      private Boolean active$value;
      @Generated
      private LocalDateTime createdAt;

      @Generated
      ShopTableBuilder() {
      }

      @Generated
      public ShopTableBuilder id(final Long id) {
         this.id = id;
         return this;
      }

      @Generated
      public ShopTableBuilder tableNumber(final Integer tableNumber) {
         this.tableNumber = tableNumber;
         return this;
      }

      @Generated
      public ShopTableBuilder label(final String label) {
         this.label = label;
         return this;
      }

      @Generated
      public ShopTableBuilder active(final Boolean active) {
         this.active$value = active;
         this.active$set = true;
         return this;
      }

      @Generated
      public ShopTableBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public ShopTable build() {
         Boolean active$value = this.active$value;
         if (!this.active$set) {
            active$value = ShopTable.$default$active();
         }

         return new ShopTable(this.id, this.tableNumber, this.label, active$value, this.createdAt);
      }

      @Generated
      public String toString() {
         Long var10000 = this.id;
         return "ShopTable.ShopTableBuilder(id=" + var10000 + ", tableNumber=" + this.tableNumber + ", label=" + this.label + ", active$value=" + this.active$value + ", createdAt=" + String.valueOf(this.createdAt) + ")";
      }
   }
}
