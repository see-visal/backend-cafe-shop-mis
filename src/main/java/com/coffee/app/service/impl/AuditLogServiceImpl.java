package com.coffee.app.service.impl;

import com.coffee.app.domain.AuditLog;
import com.coffee.app.domain.Role;
import com.coffee.app.domain.User;
import com.coffee.app.dto.response.AuditLogResponse;
import com.coffee.app.repository.AuditLogRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.AuditLogService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {
   private final AuditLogRepository auditLogRepository;
   private final UserRepository userRepository;

   private AuditLogResponse toResponse(AuditLog a) {
      User actor = this.userRepository.findByUuid(a.getActorId())
         .or(() -> this.userRepository.findByUsername(a.getActorId()))
         .orElse(null);
      List<String> actorRoles = actor != null && actor.getRoles() != null
         ? actor.getRoles().stream().map(Role::getName).toList()
         : List.of();
      String actorName = actor != null
         ? String.format(
            "%s %s",
            actor.getGivenName() != null ? actor.getGivenName() : "",
            actor.getFamilyName() != null ? actor.getFamilyName() : ""
         ).trim()
         : null;
      if (actorName != null && actorName.isBlank()) {
         actorName = actor != null ? actor.getUsername() : null;
      }

      return new AuditLogResponse(
         a.getId(),
         a.getActorId(),
         actor != null ? actor.getUsername() : null,
         actorName,
         actorRoles,
         a.getAction(),
         a.getEntity(),
         a.getEntityId(),
         a.getDetail(),
         a.getCreatedAt()
      );
   }

   @Async
   public void log(String actorId, String action, String entity, String entityId, String detail) {
      AuditLog entry = AuditLog.builder().actorId(actorId).action(action).entity(entity).entityId(entityId).detail(detail).build();
      this.auditLogRepository.save(entry);
   }

   public Page<AuditLogResponse> getAll(Pageable pageable) {
      return this.auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
   }

   public Page<AuditLogResponse> getByActor(String actorId, Pageable pageable) {
      return this.auditLogRepository.findByActorIdOrderByCreatedAtDesc(actorId, pageable).map(this::toResponse);
   }

   public Page<AuditLogResponse> getByEntity(String entity, Pageable pageable) {
      return this.auditLogRepository.findByEntityOrderByCreatedAtDesc(entity, pageable).map(this::toResponse);
   }

   public Page<AuditLogResponse> query(String actorId, String entity, LocalDateTime from, LocalDateTime to, Pageable pageable) {
      return this.auditLogRepository.queryWithFilters(actorId, entity, from, to, pageable).map(this::toResponse);
   }

   @Generated
   public AuditLogServiceImpl(final AuditLogRepository auditLogRepository, final UserRepository userRepository) {
      this.auditLogRepository = auditLogRepository;
      this.userRepository = userRepository;
   }
}
