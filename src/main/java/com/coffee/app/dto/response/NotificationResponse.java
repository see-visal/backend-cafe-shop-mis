package com.coffee.app.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String message,
        String priority,
        Boolean read,
        UUID adminId,
        UUID relatedOrderId,
        LocalDateTime createdAt
) {
}

