package com.coffee.app.service.impl;

import com.coffee.app.domain.Role;
import com.coffee.app.domain.User;
import com.coffee.app.dto.request.AdminCreateUserRequest;
import com.coffee.app.dto.request.AdminUserRequest;
import com.coffee.app.dto.response.AdminUserResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.repository.RoleRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.UserManagementService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementServiceImpl implements UserManagementService {
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;

   private User findByUuid(String uuid) {
      return (User)this.userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("User not found: " + uuid));
   }

   private AdminUserResponse toResponse(User u) {
      List<String> roles = u.getRoles() == null ? List.of() : u.getRoles().stream().map(Role::getName).toList();
      return new AdminUserResponse(u.getUuid(), u.getUsername(), u.getEmail(), u.getGivenName(), u.getFamilyName(), u.getPhoneNumber(), u.getProfileImage(), Boolean.TRUE.equals(u.getIsEnabled()), Boolean.TRUE.equals(u.getAccountNonLocked()), roles, (LocalDateTime)null);
   }

   public Page<AdminUserResponse> getAllUsers(Pageable pageable, List<String> roles) {
      if (roles != null && !roles.isEmpty()) {
         List<String> normalizedRoles = roles.stream().filter((role) -> role != null && !role.isBlank()).map((role) -> role.trim().toUpperCase(Locale.ROOT)).map((role) -> role.startsWith("ROLE_") ? role.substring(5) : role).toList();
         if (!normalizedRoles.isEmpty()) {
            return this.userRepository.findDistinctByRoles_NameIn(normalizedRoles, pageable).map(this::toResponse);
         }
      }

      return this.userRepository.findAll(pageable).map(this::toResponse);
   }

   public AdminUserResponse getUserByUuid(String uuid) {
      return this.toResponse(this.findByUuid(uuid));
   }

   @Transactional
   public AdminUserResponse createUser(AdminCreateUserRequest request) {
      if (this.userRepository.existsByUsername(request.username())) {
         throw new IllegalArgumentException("Username is already taken");
      } else if (this.userRepository.existsByEmail(request.email())) {
         throw new IllegalArgumentException("Email is already registered");
      } else {
         String roleName = request.role() != null && !request.role().isBlank() ? (request.role().startsWith("ROLE_") ? request.role().substring(5) : request.role()) : "CUSTOMER";
         Role role = (Role)this.roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
         User user = new User();
         user.setUuid(UUID.randomUUID().toString());
         user.setUsername(request.username());
         user.setEmail(request.email());
         user.setPassword(this.passwordEncoder.encode(request.password()));
         user.setGivenName(request.givenName());
         user.setFamilyName(request.familyName());
         user.setPhoneNumber(request.phoneNumber());
         user.setRoles(Set.of(role));
         user.setIsEnabled(true);
         user.setAccountNonLocked(true);
         user.setAccountNonExpired(true);
         user.setCredentialsNonExpired(true);
         return this.toResponse((User)this.userRepository.save(user));
      }
   }

   @Transactional
   public AdminUserResponse updateUser(String uuid, AdminUserRequest request) {
      User user = this.findByUuid(uuid);

      if (request.username() != null && !request.username().isBlank()) {
         String newUsername = request.username().trim();
         if (!newUsername.equals(user.getUsername())) {
            if (this.userRepository.existsByUsername(newUsername)) {
               throw new IllegalArgumentException("Username is already taken: " + newUsername);
            }
            user.setUsername(newUsername);
         }
      }

      if (request.givenName() != null && !request.givenName().isBlank()) {
         user.setGivenName(request.givenName());
      }

      if (request.familyName() != null && !request.familyName().isBlank()) {
         user.setFamilyName(request.familyName());
      }

      // Treat blank phone as null to avoid unique-constraint issues with empty strings
      String phone = request.phoneNumber() != null && !request.phoneNumber().isBlank()
            ? request.phoneNumber().trim() : null;
      user.setPhoneNumber(phone);

      String profileImage = request.profileImage() != null && !request.profileImage().isBlank()
            ? request.profileImage().trim() : null;
      user.setProfileImage(profileImage);

      if (request.password() != null && !request.password().isBlank()) {
         user.setPassword(this.passwordEncoder.encode(request.password()));
      }

      if (request.role() != null && !request.role().isBlank()) {
         String roleName = request.role().startsWith("ROLE_") ? request.role().substring(5) : request.role();
         Role role = (Role)this.roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
         user.setRoles(new HashSet<>(Set.of(role)));
      }

      return this.toResponse((User)this.userRepository.save(user));
   }

   @Transactional
   public AdminUserResponse setUserEnabled(String uuid, boolean enabled) {
      User user = this.findByUuid(uuid);
      user.setIsEnabled(enabled);
      return this.toResponse((User)this.userRepository.save(user));
   }

   @Transactional
   public AdminUserResponse setUserLocked(String uuid, boolean locked) {
      User user = this.findByUuid(uuid);
      user.setAccountNonLocked(!locked);
      return this.toResponse((User)this.userRepository.save(user));
   }

   @Transactional
   public void deleteUser(String uuid) {
      User user = this.findByUuid(uuid);
      this.userRepository.delete(user);
   }

   @Generated
   public UserManagementServiceImpl(final UserRepository userRepository, final RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
      this.passwordEncoder = passwordEncoder;
   }
}
