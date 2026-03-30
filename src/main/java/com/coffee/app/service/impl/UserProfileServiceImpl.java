package com.coffee.app.service.impl;

import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.User;
import com.coffee.app.dto.request.NotificationPreferenceRequest;
import com.coffee.app.dto.request.UpdateProfileRequest;
import com.coffee.app.dto.response.UserProfileResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.UserMapper;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.UserProfileService;

import lombok.Generated;

@Service
public class UserProfileServiceImpl implements UserProfileService {
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final UserMapper userMapper;

   public UserProfileResponse getProfile(String username) {
      return this.userMapper.toProfileResponse(this.findOrThrow(username));
   }

   @Transactional
   public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
      User user = this.findOrThrow(username);
      if (request.familyName() != null) {
         user.setFamilyName(request.familyName());
      }

      if (request.givenName() != null) {
         user.setGivenName(request.givenName());
      }

      if (request.phoneNumber() != null) {
         user.setPhoneNumber(request.phoneNumber());
      }

      if (request.gender() != null) {
         user.setGender(request.gender());
      }

      if (request.dob() != null) {
         user.setDob(request.dob());
      }

      if (request.profileImage() != null) {
         user.setProfileImage(request.profileImage());
      }

      if (request.coverImage() != null) {
         user.setCoverImage(request.coverImage());
      }

      return this.userMapper.toProfileResponse((User)this.userRepository.save(user));
   }

   @Transactional
   public UserProfileResponse updateNotificationPreference(String username, NotificationPreferenceRequest request) {
      User user = this.findOrThrow(username);
      String preference = request.preference().trim().toUpperCase();
      if (!Set.of("IN_APP", "TELEGRAM").contains(preference)) {
         throw new IllegalArgumentException("Unsupported notification preference: " + request.preference());
      } else {
         String incomingChatId = request.telegramChatId();
         String chatId = incomingChatId != null ? incomingChatId.trim() : null;
         if ("TELEGRAM".equals(preference) && (chatId == null || chatId.isBlank()) && (user.getTelegramChatId() == null || user.getTelegramChatId().isBlank())) {
            throw new IllegalArgumentException("Telegram chat id is required when preference is TELEGRAM");
         } else {
            user.setNotificationPreference(preference);
            if (chatId != null) {
               user.setTelegramChatId(chatId.isBlank() ? null : chatId);
            }

            return this.userMapper.toProfileResponse((User)this.userRepository.save(user));
         }
      }
   }

   @Transactional
   public void changePassword(String username, String oldPassword, String newPassword) {
      User user = this.findOrThrow(username);
      if (!this.passwordEncoder.matches(oldPassword, user.getPassword())) {
         throw new BadCredentialsException("Current password is incorrect.");
      } else {
         user.setPassword(this.passwordEncoder.encode(newPassword));
         this.userRepository.save(user);
      }
   }

   private User findOrThrow(String username) {
      return (User)this.userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
   }

   @Generated
   public UserProfileServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final UserMapper userMapper) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
      this.userMapper = userMapper;
   }
}
