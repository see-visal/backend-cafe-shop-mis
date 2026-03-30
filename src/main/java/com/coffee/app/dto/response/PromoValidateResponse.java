package com.coffee.app.dto.response;

import java.math.BigDecimal;

public record PromoValidateResponse(String code, String discountType, BigDecimal discountValue, BigDecimal discountAmount, String message) {
}
