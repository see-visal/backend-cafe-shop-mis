package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(@NotNull UUID orderId, @NotBlank String paymentMethod, @NotNull BigDecimal amount, String transactionRef) {
}
