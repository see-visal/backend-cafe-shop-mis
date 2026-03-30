package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(UUID id, UUID userId, String status, String orderType, Integer tableNumber, BigDecimal totalPrice, String paymentRef, String notes, UUID baristaId, Integer estimatedMinutes, LocalDateTime createdAt, LocalDateTime servedAt, List<OrderItemResponse> items, String clientSecret, String pickupToken, BigDecimal discountAmount, String promoCode) {
}
