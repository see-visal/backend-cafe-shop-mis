package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record StockAdjustRequest(@NotNull UUID ingredientId, @NotNull BigDecimal delta, String reason) {
}
