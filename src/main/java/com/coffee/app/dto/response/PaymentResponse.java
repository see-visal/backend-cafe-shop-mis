package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, String paymentMethod, String status, BigDecimal amount, String transactionRef, LocalDateTime paidAt, LocalDateTime createdAt) {
}
