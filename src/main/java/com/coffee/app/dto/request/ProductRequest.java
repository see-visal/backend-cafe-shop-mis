package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequest(
   @NotBlank String name,
   String description,
   @NotNull @Positive BigDecimal price,
   Integer categoryId,
   String imageUrl,
   Boolean showOnHomepage,
   Boolean todaySpecial,
   Integer homePriority
) {
}
