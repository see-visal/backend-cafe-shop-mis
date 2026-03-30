package com.coffee.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RatingRequest(@NotNull UUID productId, @NotNull @Min(1L) @Max(5L) Integer stars, @Size(
   max = 500
) String comment) {
}
