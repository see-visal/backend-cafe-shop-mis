package com.coffee.app.security;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import jakarta.annotation.Nullable;
import lombok.Generated;

@JsonTypeInfo(
   use = Id.CLASS
)
@JsonIgnoreProperties(
   ignoreUnknown = true
)
public class CustomUserDetails implements UserDetails {
   private final String uuid;
   private final String username;
   private final String email;
   private final String password;
   private final String familyName;
   private final String givenName;
   private final String phoneNumber;
   private final String gender;
   private final LocalDate dob;
   private final String profileImage;
   private final String coverImage;
   private final Boolean accountNonExpired;
   private final Boolean accountNonLocked;
   private final Boolean credentialsNonExpired;
   private final Boolean isEnabled;
   private final Set<String> roles;
   private final Set<String> permissions;

   public CustomUserDetails(@JsonProperty("uuid") String uuid, @JsonProperty("username") String username, @JsonProperty("email") String email, @JsonProperty("password") String password, @JsonProperty("familyName") String familyName, @JsonProperty("givenName") String givenName, @JsonProperty("phoneNumber") String phoneNumber, @JsonProperty("gender") String gender, @JsonProperty("dob") LocalDate dob, @JsonProperty("profileImage") String profileImage, @JsonProperty("coverImage") String coverImage, @JsonProperty("accountNonExpired") Boolean accountNonExpired, @JsonProperty("accountNonLocked") Boolean accountNonLocked, @JsonProperty("credentialsNonExpired") Boolean credentialsNonExpired, @JsonProperty("isEnabled") Boolean isEnabled, @JsonProperty("roles") Set<String> roles, @JsonProperty("permission") Set<String> permissions) {
      this.uuid = uuid;
      this.username = username;
      this.email = email;
      this.password = password;
      this.familyName = familyName;
      this.givenName = givenName;
      this.phoneNumber = phoneNumber;
      this.gender = gender;
      this.dob = dob;
      this.profileImage = profileImage;
      this.coverImage = coverImage;
      this.accountNonExpired = accountNonExpired;
      this.accountNonLocked = accountNonLocked;
      this.credentialsNonExpired = credentialsNonExpired;
      this.isEnabled = isEnabled;
      this.roles = roles;
      this.permissions = permissions;
   }

   public Collection<? extends GrantedAuthority> getAuthorities() {
      List<GrantedAuthority> authorities = new ArrayList<>();
      if (this.roles != null) {
         this.roles.forEach((role) -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
      }

      if (this.permissions != null) {
         this.permissions.forEach((perm) -> authorities.add(new SimpleGrantedAuthority(perm)));
      }

      return authorities;
   }

   @Nullable
   public String getPassword() {
      return this.password;
   }

   public String getUsername() {
      return this.username;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CustomUserDetails that = (CustomUserDetails)o;
         return Objects.equals(this.getUsername(), that.getUsername());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getUsername()});
   }

   public boolean isAccountNonExpired() {
      return Boolean.TRUE.equals(this.accountNonExpired);
   }

   public boolean isAccountNonLocked() {
      return Boolean.TRUE.equals(this.accountNonLocked);
   }

   public boolean isCredentialsNonExpired() {
      return Boolean.TRUE.equals(this.credentialsNonExpired);
   }

   public boolean isEnabled() {
      return Boolean.TRUE.equals(this.isEnabled);
   }

   @Generated
   public String getUuid() {
      return this.uuid;
   }

   @Generated
   public String getEmail() {
      return this.email;
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
   public Set<String> getRoles() {
      return this.roles;
   }

   @Generated
   public Set<String> getPermissions() {
      return this.permissions;
   }
}
