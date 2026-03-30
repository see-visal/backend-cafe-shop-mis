package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Generated;

@Entity
@Table(
   name = "categories"
)
public class Category {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Integer id;
   @Column(
      nullable = false,
      unique = true,
      length = 80
   )
   private String name;
   @Column(
      length = 50
   )
   private String icon;
   @Column(
      length = 512
   )
   private String imageUrl;

   @Generated
   public Integer getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getIcon() {
      return this.icon;
   }

   @Generated
   public String getImageUrl() {
      return this.imageUrl;
   }

   @Generated
   public void setId(final Integer id) {
      this.id = id;
   }

   @Generated
   public void setName(final String name) {
      this.name = name;
   }

   @Generated
   public void setIcon(final String icon) {
      this.icon = icon;
   }

   @Generated
   public void setImageUrl(final String imageUrl) {
      this.imageUrl = imageUrl;
   }
}
