package com.coffee.app.service;

import com.coffee.app.dto.response.AuditLogResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
   void log(String actorId, String action, String entity, String entityId, String detail);

   Page<AuditLogResponse> getAll(Pageable pageable);

   Page<AuditLogResponse> getByActor(String actorId, Pageable pageable);

   Page<AuditLogResponse> getByEntity(String entity, Pageable pageable);

   Page<AuditLogResponse> query(String actorId, String entity, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
