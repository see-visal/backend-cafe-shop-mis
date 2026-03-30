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
import java.time.LocalDateTime;
import lombok.Generated;

@Entity
@Table(
   name = "admins"
)
public class Admin {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @OneToOne(
      fetch = FetchType.LAZY,
      optional = false
   )
   @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      nullable = false,
      unique = true
   )
   private User user;
   @Column(
      length = 100
   )
   private String displayName;
   @Column(
      length = 100
   )
   private String department;
   @Column(
      name = "created_at",
      updatable = false
   )
   private LocalDateTime createdAt;
   @Column(
      name = "last_login_at"
   )
   private LocalDateTime lastLoginAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }

   @Generated
   public static AdminBuilder builder() {
      return new AdminBuilder();
   }

   @Generated
   public Long getId() {
      return this.id;
   }

   @Generated
   public User getUser() {
      return this.user;
   }

   @Generated
   public String getDisplayName() {
      return this.displayName;
   }

   @Generated
   public String getDepartment() {
      return this.department;
   }

   @Generated
   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   @Generated
   public LocalDateTime getLastLoginAt() {
      return this.lastLoginAt;
   }

   @Generated
   public void setId(final Long id) {
      this.id = id;
   }

   @Generated
   public void setUser(final User user) {
      this.user = user;
   }

   @Generated
   public void setDisplayName(final String displayName) {
      this.displayName = displayName;
   }

   @Generated
   public void setDepartment(final String department) {
      this.department = department;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public void setLastLoginAt(final LocalDateTime lastLoginAt) {
      this.lastLoginAt = lastLoginAt;
   }

   @Generated
   public Admin() {
   }

   @Generated
   public Admin(final Long id, final User user, final String displayName, final String department, final LocalDateTime createdAt, final LocalDateTime lastLoginAt) {
      this.id = id;
      this.user = user;
      this.displayName = displayName;
      this.department = department;
      this.createdAt = createdAt;
      this.lastLoginAt = lastLoginAt;
   }

   @Generated
   public static class AdminBuilder {
      @Generated
      private Long id;
      @Generated
      private User user;
      @Generated
      private String displayName;
      @Generated
      private String department;
      @Generated
      private LocalDateTime createdAt;
      @Generated
      private LocalDateTime lastLoginAt;

      @Generated
      AdminBuilder() {
      }

      @Generated
      public AdminBuilder id(final Long id) {
         this.id = id;
         return this;
      }

      @Generated
      public AdminBuilder user(final User user) {
         this.user = user;
         return this;
      }

      @Generated
      public AdminBuilder displayName(final String displayName) {
         this.displayName = displayName;
         return this;
      }

      @Generated
      public AdminBuilder department(final String department) {
         this.department = department;
         return this;
      }

      @Generated
      public AdminBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public AdminBuilder lastLoginAt(final LocalDateTime lastLoginAt) {
         this.lastLoginAt = lastLoginAt;
         return this;
      }

      @Generated
      public Admin build() {
         return new Admin(this.id, this.user, this.displayName, this.department, this.createdAt, this.lastLoginAt);
      }

      @Generated
      public String toString() {
         Long var10000 = this.id;
         return "Admin.AdminBuilder(id=" + var10000 + ", user=" + String.valueOf(this.user) + ", displayName=" + this.displayName + ", department=" + this.department + ", createdAt=" + String.valueOf(this.createdAt) + ", lastLoginAt=" + String.valueOf(this.lastLoginAt) + ")";
      }
   }
}
