package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record IngredientResponse(UUID id, String name, String unit, BigDecimal stockQty, BigDecimal lowThreshold, String supplier, String imageUrl, boolean lowStock) {
}
