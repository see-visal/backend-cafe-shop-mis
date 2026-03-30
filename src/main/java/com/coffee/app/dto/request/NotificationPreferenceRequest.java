package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NotificationPreferenceRequest(@NotBlank @Pattern(
   regexp = "IN_APP|TELEGRAM",
   message = "Preference must be IN_APP or TELEGRAM"
) String preference, @Size(
   max = 50
) String telegramChatId) {
}