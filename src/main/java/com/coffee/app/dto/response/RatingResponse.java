package com.coffee.app.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RatingResponse(UUID id, UUID orderId, UUID productId, String productName, Integer stars, String comment, LocalDateTime createdAt) {
}
