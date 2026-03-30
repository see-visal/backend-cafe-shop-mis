package com.coffee.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(@NotBlank @Size(
   max = 80
) String name, @Size(
   max = 50
) String icon, @Size(
   max = 512
) String imageUrl) {
}
