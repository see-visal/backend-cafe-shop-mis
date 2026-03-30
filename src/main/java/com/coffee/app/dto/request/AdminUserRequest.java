package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserRequest(@NotBlank @Size(
   min = 3,
   max = 64
) String username, @NotBlank String givenName, @NotBlank String familyName, String phoneNumber, @Size(
   max = 255
) String profileImage, String role, @Size(
   min = 6
) String password) {
}
