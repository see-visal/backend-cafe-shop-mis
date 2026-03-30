package com.coffee.app.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record UserProfileResponse(String uuid, String username, String email, String familyName, String givenName, String phoneNumber, String gender, LocalDate dob, String profileImage, String coverImage, Set<String> roles, Integer loyaltyPoints, String notificationPreference) {
}
