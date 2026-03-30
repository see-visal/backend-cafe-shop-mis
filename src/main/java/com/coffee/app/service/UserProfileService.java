package com.coffee.app.service;

import com.coffee.app.dto.request.NotificationPreferenceRequest;
import com.coffee.app.dto.request.UpdateProfileRequest;
import com.coffee.app.dto.response.UserProfileResponse;

public interface UserProfileService {
   UserProfileResponse getProfile(String username);

   UserProfileResponse updateProfile(String username, UpdateProfileRequest request);

   UserProfileResponse updateNotificationPreference(String username, NotificationPreferenceRequest request);

   void changePassword(String username, String oldPassword, String newPassword);
}
