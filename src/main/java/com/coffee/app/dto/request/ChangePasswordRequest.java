package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank @Size(
   min = 6,
   max = 128
) String newPassword, @NotBlank String confirmPassword) {
}