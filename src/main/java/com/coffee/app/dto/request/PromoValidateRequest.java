package com.coffee.app.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record PromoValidateRequest(@NotBlank String code, @DecimalMin("0.0") BigDecimal orderTotal) {
}
