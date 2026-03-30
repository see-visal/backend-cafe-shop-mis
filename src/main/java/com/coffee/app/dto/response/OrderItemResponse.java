package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(UUID id, UUID productId, String productName, String productImageUrl, int quantity, BigDecimal unitPrice, String specialInstructions) {
}
