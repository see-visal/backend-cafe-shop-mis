package com.coffee.app.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record IngredientRequest(@NotBlank @Size(
   max = 100
) String name, @NotBlank @Size(
   max = 20
) String unit, @NotNull @DecimalMin("0") BigDecimal stockQty, @NotNull @DecimalMin("0") BigDecimal lowThreshold, @Size(
   max = 150
) String supplier, @Size(
   max = 512
) String imageUrl) {
}
