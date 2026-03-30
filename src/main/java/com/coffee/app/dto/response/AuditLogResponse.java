package com.coffee.app.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AuditLogResponse(
   Long id,
   String actorId,
   String actorUsername,
   String actorName,
   List<String> actorRoles,
   String action,
   String entity,
   String entityId,
   String detail,
   LocalDateTime createdAt
) {
}
