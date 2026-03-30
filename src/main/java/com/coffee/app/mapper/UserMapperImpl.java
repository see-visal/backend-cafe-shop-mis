package com.coffee.app.mapper;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coffee.app.domain.User;
import com.coffee.app.dto.response.UserProfileResponse;

@Component
public class UserMapperImpl implements UserMapper {
   public UserProfileResponse toProfileResponse(User user) {
      if (user == null) {
         return null;
      } else {
         String uuid = null;
         String username = null;
         String email = null;
         String familyName = null;
         String givenName = null;
         String phoneNumber = null;
         String gender = null;
         LocalDate dob = null;
         String profileImage = null;
         String coverImage = null;
         uuid = user.getUuid();
         username = user.getUsername();
         email = user.getEmail();
         familyName = user.getFamilyName();
         givenName = user.getGivenName();
         phoneNumber = user.getPhoneNumber();
         gender = user.getGender();
         dob = user.getDob();
         profileImage = user.getProfileImage();
         coverImage = user.getCoverImage();
         Set<String> roles = user.getRoles().stream().map((r) -> r.getName()).collect(Collectors.toSet());
         Integer loyaltyPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
         String notificationPreference = user.getNotificationPreference() != null ? user.getNotificationPreference() : "IN_APP";
         UserProfileResponse userProfileResponse = new UserProfileResponse(uuid, username, email, familyName, givenName, phoneNumber, gender, dob, profileImage, coverImage, roles, loyaltyPoints, notificationPreference);
         return userProfileResponse;
      }
   }
}
