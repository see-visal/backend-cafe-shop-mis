package com.coffee.app.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.Permission;
import com.coffee.app.domain.Role;
import com.coffee.app.domain.User;
import com.coffee.app.repository.PermissionRepository;
import com.coffee.app.repository.RoleRepository;
import com.coffee.app.repository.UserRepository;

import lombok.Generated;

@Component
public class SecurityInit implements ApplicationRunner {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(SecurityInit.class);
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PermissionRepository permissionRepository;
   private final PasswordEncoder passwordEncoder;
   private static final List<String> ALL_PERMISSIONS = List.of("READ_PRODUCT", "WRITE_PRODUCT", "READ_ORDER", "WRITE_ORDER", "READ_USER", "WRITE_USER", "READ_INGREDIENT", "WRITE_INGREDIENT", "READ_PAYMENT", "WRITE_PAYMENT");
   private static final List<String> BARISTA_PERMISSIONS = List.of("READ_ORDER", "WRITE_ORDER", "READ_PRODUCT", "READ_INGREDIENT", "WRITE_INGREDIENT");
   private static final List<String> CUSTOMER_PERMISSIONS = List.of("READ_PRODUCT", "READ_ORDER", "WRITE_ORDER", "READ_PAYMENT", "WRITE_PAYMENT");

   @Override
   @Transactional
   public void run(ApplicationArguments args) {
      log.info("=== SecurityInit: seeding permissions, roles and default users ===");
      ALL_PERMISSIONS.forEach((name) -> {
         if (this.permissionRepository.findByName(name).isEmpty()) {
            Permission p = new Permission();
            p.setName(name);
            this.permissionRepository.save(p);
            log.info("  ✅ Permission created: {}", name);
         }

      });
      Role adminRole = this.seedRole("ADMIN", ALL_PERMISSIONS);
      Role baristaRole = this.seedRole("BARISTA", BARISTA_PERMISSIONS);
      this.seedRole("CUSTOMER", CUSTOMER_PERMISSIONS);
      if (!this.userRepository.existsByUsername("admin")) {
         this.userRepository.save(this.buildUser("admin", "admin@coffeeshop.com", "admin123", "Admin", "Shop", "0000000000", adminRole));
         log.info("  ✅ admin created   [username=admin    password=admin123]");
      } else {
         log.info("  ℹ️  admin already exists — skipped");
      }

      if (!this.userRepository.existsByUsername("barista")) {
         this.userRepository.save(this.buildUser("barista", "barista@coffeeshop.com", "barista123", "Barista", "Staff", "0000000001", baristaRole));
         log.info("  ✅ barista created  [username=barista  password=barista123]");
      } else {
         log.info("  ℹ️  barista already exists — skipped");
      }

      log.info("=== SecurityInit: done ===");
   }

   private Role seedRole(String roleName, List<String> permNames) {
      return (Role)this.roleRepository.findByName(roleName).orElseGet(() -> {
         Set<Permission> perms = permNames.stream().map((n) -> (Permission)this.permissionRepository.findByName(n).orElseThrow(() -> new IllegalStateException("Permission not found: " + n))).collect(Collectors.toSet());
         Role role = new Role();
         role.setName(roleName);
         role.setPermissions(perms);
         Role saved = (Role)this.roleRepository.save(role);
         log.info("  ✅ Role created: {}", roleName);
         return saved;
      });
   }

   private User buildUser(String username, String email, String rawPassword, String givenName, String familyName, String phone, Role role) {
      User u = new User();
      u.setUuid(UUID.randomUUID().toString());
      u.setUsername(username);
      u.setEmail(email);
      u.setPassword(this.passwordEncoder.encode(rawPassword));
      u.setGivenName(givenName);
      u.setFamilyName(familyName);
      u.setPhoneNumber(phone);
      u.setGender("N/A");
      u.setProfileImage("");
      u.setCoverImage("");
      u.setAccountNonExpired(true);
      u.setAccountNonLocked(true);
      u.setCredentialsNonExpired(true);
      u.setIsEnabled(true);
      u.setRoles(Set.of(role));
      u.setPermissions(new HashSet<>());
      return u;
   }

   @Generated
   public SecurityInit(final UserRepository userRepository, final RoleRepository roleRepository, final PermissionRepository permissionRepository, final PasswordEncoder passwordEncoder) {
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
      this.permissionRepository = permissionRepository;
      this.passwordEncoder = passwordEncoder;
   }
}
