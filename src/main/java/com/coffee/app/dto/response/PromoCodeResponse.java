package com.coffee.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PromoCodeResponse(UUID id, String code, String discountType, BigDecimal discountValue, Integer maxUses, Integer usedCount, LocalDateTime expiresAt, Boolean active, LocalDateTime createdAt) {
}
