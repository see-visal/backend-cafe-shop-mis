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
import java.util.Set;
import lombok.Generated;

@Entity
@Table(
   name = "roles"
)
public class Role {
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
      mappedBy = "roles"
   )
   private Set<User> users;
   @ManyToMany(
      fetch = FetchType.EAGER
   )
   @JoinTable(
      name = "roles_permissions",
      joinColumns = {@JoinColumn(
   name = "role_id"
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
   public String getName() {
      return this.name;
   }

   @Generated
   public Set<User> getUsers() {
      return this.users;
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
   public void setName(final String name) {
      this.name = name;
   }

   @Generated
   public void setUsers(final Set<User> users) {
      this.users = users;
   }

   @Generated
   public void setPermissions(final Set<Permission> permissions) {
      this.permissions = permissions;
   }
}
