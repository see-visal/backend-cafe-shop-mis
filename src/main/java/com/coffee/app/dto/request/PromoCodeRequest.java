package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromoCodeRequest(@NotBlank String code, @NotBlank String discountType, @NotNull @Positive BigDecimal discountValue, Integer maxUses, LocalDateTime expiresAt) {
}
