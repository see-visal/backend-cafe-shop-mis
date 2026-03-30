package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.Generated;

@Entity
@Table(
   name = "auth_users"
)
public class User {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      nullable = false,
      unique = true
   )
   private String uuid;
   @Column(
      nullable = false,
      unique = true,
      length = 64
   )
   private String username;
   @Column(
      nullable = false,
      unique = true,
      length = 256
   )
   private String email;
   @Column(
      nullable = false,
      length = 256
   )
   private String password;
   @Column(
      nullable = false,
      columnDefinition = "Text"
   )
   private String familyName;
   @Column(
      nullable = false,
      columnDefinition = "Text"
   )
   private String givenName;
   @Column(
      unique = true
   )
   private String phoneNumber;
   private String gender;
   private LocalDate dob;
   @Column(
      length = 256
   )
   private String profileImage;
   @Column(
      length = 256
   )
   private String coverImage;
   @Column(
      name = "loyalty_points",
      columnDefinition = "INT DEFAULT 0"
   )
   private Integer loyaltyPoints = 0;
   @Column(
      name = "notification_preference",
      length = 20,
      columnDefinition = "VARCHAR(20) DEFAULT 'IN_APP'"
   )
   private String notificationPreference = "IN_APP";
   @Column(
      name = "telegram_chat_id",
      length = 50
   )
   private String telegramChatId;
   private Boolean accountNonExpired;
   @Column(
      columnDefinition = "BOOLEAN DEFAULT TRUE"
   )
   private Boolean accountNonLocked;
   @Column(
      columnDefinition = "BOOLEAN DEFAULT TRUE"
   )
   private Boolean credentialsNonExpired;
   @Column(
      columnDefinition = "BOOLEAN DEFAULT TRUE"
   )
   private Boolean isEnabled;
   @ManyToMany(
      fetch = FetchType.EAGER
   )
   @JoinTable(
      name = "users_roles",
      joinColumns = {@JoinColumn(
   name = "user_id"
)},
      inverseJoinColumns = {@JoinColumn(
   name = "role_id"
)}
   )
   private Set<Role> roles;
   @ManyToMany(
      fetch = FetchType.EAGER
   )
   @JoinTable(
      name = "users_permissions",
      joinColumns = {@JoinColumn(
   name = "user_id"
)},
      inverseJoinColumns = {@JoinColumn(
   name = "permission_id"
)}
   )
   private Set<Permission> permissions;

   @Generated
   public Long getId() {
      return this.id;
   }

   @Generated
   public String getUuid() {
      return this.uuid;
   }

   @Generated
   public String getUsername() {
      return this.username;
   }

   @Generated
   public String getEmail() {
      return this.email;
   }

   @Generated
   public String getPassword() {
      return this.password;
   }

   @Generated
   public String getFamilyName() {
      return this.familyName;
   }

   @Generated
   public String getGivenName() {
      return this.givenName;
   }

   @Generated
   public String getPhoneNumber() {
      return this.phoneNumber;
   }

   @Generated
   public String getGender() {
      return this.gender;
   }

   @Generated
   public LocalDate getDob() {
      return this.dob;
   }

   @Generated
   public String getProfileImage() {
      return this.profileImage;
   }

   @Generated
   public String getCoverImage() {
      return this.coverImage;
   }

   @Generated
   public Integer getLoyaltyPoints() {
      return this.loyaltyPoints;
   }

   @Generated
   public String getNotificationPreference() {
      return this.notificationPreference;
   }

   @Generated
   public String getTelegramChatId() {
      return this.telegramChatId;
   }

   @Generated
   public Boolean getAccountNonExpired() {
      return this.accountNonExpired;
   }

   @Generated
   public Boolean getAccountNonLocked() {
      return this.accountNonLocked;
   }

   @Generated
   public Boolean getCredentialsNonExpired() {
      return this.credentialsNonExpired;
   }

   @Generated
   public Boolean getIsEnabled() {
      return this.isEnabled;
   }

   @Generated
   public Set<Role> getRoles() {
      return this.roles;
   }

   @Generated
   public Set<Permission> getPermissions() {
      return this.permissions;
   }

   @Generated
   public void setId(final Long id) {
      this.id = id;
   }

   @Generated
   public void setUuid(final String uuid) {
      this.uuid = uuid;
   }

   @Generated
   public void setUsername(final String username) {
      this.username = username;
   }

   @Generated
   public void setEmail(final String email) {
      this.email = email;
   }

   @Generated
   public void setPassword(final String password) {
      this.password = password;
   }

   @Generated
   public void setFamilyName(final String familyName) {
      this.familyName = familyName;
   }

   @Generated
   public void setGivenName(final String givenName) {
      this.givenName = givenName;
   }

   @Generated
   public void setPhoneNumber(final String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   @Generated
   public void setGender(final String gender) {
      this.gender = gender;
   }

   @Generated
   public void setDob(final LocalDate dob) {
      this.dob = dob;
   }

   @Generated
   public void setProfileImage(final String profileImage) {
      this.profileImage = profileImage;
   }

   @Generated
   public void setCoverImage(final String coverImage) {
      this.coverImage = coverImage;
   }

   @Generated
   public void setLoyaltyPoints(final Integer loyaltyPoints) {
      this.loyaltyPoints = loyaltyPoints;
   }

   @Generated
   public void setNotificationPreference(final String notificationPreference) {
      this.notificationPreference = notificationPreference;
   }

   @Generated
   public void setTelegramChatId(final String telegramChatId) {
      this.telegramChatId = telegramChatId;
   }

   @Generated
   public void setAccountNonExpired(final Boolean accountNonExpired) {
      this.accountNonExpired = accountNonExpired;
   }

   @Generated
   public void setAccountNonLocked(final Boolean accountNonLocked) {
      this.accountNonLocked = accountNonLocked;
   }

   @Generated
   public void setCredentialsNonExpired(final Boolean credentialsNonExpired) {
      this.credentialsNonExpired = credentialsNonExpired;
   }

   @Generated
   public void setIsEnabled(final Boolean isEnabled) {
      this.isEnabled = isEnabled;
   }

   @Generated
   public void setRoles(final Set<Role> roles) {
      this.roles = roles;
   }

   @Generated
   public void setPermissions(final Set<Permission> permissions) {
      this.permissions = permissions;
   }
}
