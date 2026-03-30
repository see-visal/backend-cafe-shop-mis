package com.coffee.app.dto.response;

import java.time.LocalDateTime;

public record ShopTableResponse(Long id, Integer tableNumber, String label, Boolean active, LocalDateTime createdAt) {
}