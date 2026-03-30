package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Generated;

@Entity
@Table(
   name = "permissions"
)
public class Permission {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      nullable = false,
      unique = true
   )
   private String name;
   @ManyToMany(
      mappedBy = "permissions"
   )
   private List<User> users;
   @ManyToMany(
      mappedBy = "permissions"
   )
   private List<Role> roles;

   @Generated
   public Long getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public List<User> getUsers() {
      return this.users;
   }

   @Generated
   public List<Role> getRoles() {
      return this.roles;
   }

   @Generated
   public void setId(final Long id) {
      this.id = id;
   }

   @Generated
   public void setName(final String name) {
      this.name = name;
   }

   @Generated
   public void setUsers(final List<User> users) {
      this.users = users;
   }

   @Generated
   public void setRoles(final List<Role> roles) {
      this.roles = roles;
   }
}
