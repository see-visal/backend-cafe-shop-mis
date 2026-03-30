package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
   UUID id,
   String name,
   String description,
   BigDecimal price,
   String category,
   String imageUrl,
   boolean active,
   boolean showOnHomepage,
   boolean todaySpecial,
   Integer homePriority
) {
}
