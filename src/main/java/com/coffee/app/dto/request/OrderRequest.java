package com.coffee.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderRequest(UUID userId, @NotNull String orderType, Integer tableNumber, @NotEmpty @Valid List<OrderItemRequest> items, String notes, String promoCode, BigDecimal discountAmount) {
}
