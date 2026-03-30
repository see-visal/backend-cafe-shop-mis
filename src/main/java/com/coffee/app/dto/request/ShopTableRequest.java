package com.coffee.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShopTableRequest(@NotNull @Min(
   value = 1,
   message = "Table number must be at least 1"
) @Max(
   value = 999,
   message = "Table number must be 999 or less"
) Integer tableNumber, @Size(
   max = 50
) String label, Boolean active) {
}