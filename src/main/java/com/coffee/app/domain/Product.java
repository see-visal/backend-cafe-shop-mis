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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Generated;

@Entity
@Table(
   name = "products"
)
public class Product {
   @Id
   @GeneratedValue(
      strategy = GenerationType.UUID
   )
   private UUID id;
   @Column(
      nullable = false,
      length = 120
   )
   private String name;
   @Column(
      columnDefinition = "TEXT"
   )
   private String description;
   @Column(
      nullable = false,
      precision = 10,
      scale = 2
   )
   private BigDecimal price;
   @ManyToOne(
      fetch = FetchType.LAZY
   )
   @JoinColumn(
      name = "category_id"
   )
   private Category category;
   @Column(
      name = "image_url",
      length = 255
   )
   private String imageUrl;
   @Column(
      name = "show_on_homepage",
      nullable = false
   )
   private boolean showOnHomepage;
   @Column(
      name = "today_special",
      nullable = false
   )
   private boolean todaySpecial;
   @Column(
      name = "home_priority"
   )
   private Integer homePriority;
   private boolean active;
   @Column(
      name = "created_at",
      updatable = false
   )
   private LocalDateTime createdAt;
   @Column(
      name = "updated_at"
   )
   private LocalDateTime updatedAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
      this.updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
      this.updatedAt = LocalDateTime.now();
   }

   @Generated
   private static boolean $default$active() {
      return true;
   }

   @Generated
   private static boolean $default$showOnHomepage() {
      return false;
   }

   @Generated
   private static boolean $default$todaySpecial() {
      return false;
   }

   @Generated
   public static ProductBuilder builder() {
      return new ProductBuilder();
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
   public String getDescription() {
      return this.description;
   }

   @Generated
   public BigDecimal getPrice() {
      return this.price;
   }

   @Generated
   public Category getCategory() {
      return this.category;
   }

   @Generated
   public String getImageUrl() {
      return this.imageUrl;
   }

   @Generated
   public boolean isShowOnHomepage() {
      return this.showOnHomepage;
   }

   @Generated
   public boolean isTodaySpecial() {
      return this.todaySpecial;
   }

   @Generated
   public Integer getHomePriority() {
      return this.homePriority;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   @Generated
   public LocalDateTime getUpdatedAt() {
      return this.updatedAt;
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
   public void setDescription(final String description) {
      this.description = description;
   }

   @Generated
   public void setPrice(final BigDecimal price) {
      this.price = price;
   }

   @Generated
   public void setCategory(final Category category) {
      this.category = category;
   }

   @Generated
   public void setImageUrl(final String imageUrl) {
      this.imageUrl = imageUrl;
   }

   @Generated
   public void setShowOnHomepage(final boolean showOnHomepage) {
      this.showOnHomepage = showOnHomepage;
   }

   @Generated
   public void setTodaySpecial(final boolean todaySpecial) {
      this.todaySpecial = todaySpecial;
   }

   @Generated
   public void setHomePriority(final Integer homePriority) {
      this.homePriority = homePriority;
   }

   @Generated
   public void setActive(final boolean active) {
      this.active = active;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public void setUpdatedAt(final LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
   }

   @Generated
   public Product() {
      this.showOnHomepage = $default$showOnHomepage();
      this.todaySpecial = $default$todaySpecial();
      this.active = $default$active();
   }

   @Generated
   public Product(final UUID id, final String name, final String description, final BigDecimal price, final Category category, final String imageUrl, final boolean showOnHomepage, final boolean todaySpecial, final Integer homePriority, final boolean active, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.category = category;
      this.imageUrl = imageUrl;
      this.showOnHomepage = showOnHomepage;
      this.todaySpecial = todaySpecial;
      this.homePriority = homePriority;
      this.active = active;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
   }

   @Generated
   public static class ProductBuilder {
      @Generated
      private UUID id;
      @Generated
      private String name;
      @Generated
      private String description;
      @Generated
      private BigDecimal price;
      @Generated
      private Category category;
      @Generated
      private String imageUrl;
      @Generated
      private boolean showOnHomepage$set;
      @Generated
      private boolean showOnHomepage$value;
      @Generated
      private boolean todaySpecial$set;
      @Generated
      private boolean todaySpecial$value;
      @Generated
      private Integer homePriority;
      @Generated
      private boolean active$set;
      @Generated
      private boolean active$value;
      @Generated
      private LocalDateTime createdAt;
      @Generated
      private LocalDateTime updatedAt;

      @Generated
      ProductBuilder() {
      }

      @Generated
      public ProductBuilder id(final UUID id) {
         this.id = id;
         return this;
      }

      @Generated
      public ProductBuilder name(final String name) {
         this.name = name;
         return this;
      }

      @Generated
      public ProductBuilder description(final String description) {
         this.description = description;
         return this;
      }

      @Generated
      public ProductBuilder price(final BigDecimal price) {
         this.price = price;
         return this;
      }

      @Generated
      public ProductBuilder category(final Category category) {
         this.category = category;
         return this;
      }

      @Generated
      public ProductBuilder imageUrl(final String imageUrl) {
         this.imageUrl = imageUrl;
         return this;
      }

      @Generated
      public ProductBuilder showOnHomepage(final boolean showOnHomepage) {
         this.showOnHomepage$value = showOnHomepage;
         this.showOnHomepage$set = true;
         return this;
      }

      @Generated
      public ProductBuilder todaySpecial(final boolean todaySpecial) {
         this.todaySpecial$value = todaySpecial;
         this.todaySpecial$set = true;
         return this;
      }

      @Generated
      public ProductBuilder homePriority(final Integer homePriority) {
         this.homePriority = homePriority;
         return this;
      }

      @Generated
      public ProductBuilder active(final boolean active) {
         this.active$value = active;
         this.active$set = true;
         return this;
      }

      @Generated
      public ProductBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public ProductBuilder updatedAt(final LocalDateTime updatedAt) {
         this.updatedAt = updatedAt;
         return this;
      }

      @Generated
      public Product build() {
         boolean showOnHomepage$value = this.showOnHomepage$value;
         if (!this.showOnHomepage$set) {
            showOnHomepage$value = Product.$default$showOnHomepage();
         }

         boolean todaySpecial$value = this.todaySpecial$value;
         if (!this.todaySpecial$set) {
            todaySpecial$value = Product.$default$todaySpecial();
         }

         boolean active$value = this.active$value;
         if (!this.active$set) {
            active$value = Product.$default$active();
         }

         return new Product(this.id, this.name, this.description, this.price, this.category, this.imageUrl, showOnHomepage$value, todaySpecial$value, this.homePriority, active$value, this.createdAt, this.updatedAt);
      }

      @Generated
      public String toString() {
         String var10000 = String.valueOf(this.id);
         return "Product.ProductBuilder(id=" + var10000 + ", name=" + this.name + ", description=" + this.description + ", price=" + String.valueOf(this.price) + ", category=" + String.valueOf(this.category) + ", imageUrl=" + this.imageUrl + ", showOnHomepage$value=" + this.showOnHomepage$value + ", todaySpecial$value=" + this.todaySpecial$value + ", homePriority=" + String.valueOf(this.homePriority) + ", active$value=" + this.active$value + ", createdAt=" + String.valueOf(this.createdAt) + ", updatedAt=" + String.valueOf(this.updatedAt) + ")";
      }
   }
}
