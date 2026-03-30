package com.coffee.app.repository;

import com.coffee.app.domain.AuditLog;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
   Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

   Page<AuditLog> findByActorIdOrderByCreatedAtDesc(String actorId, Pageable pageable);

   Page<AuditLog> findByEntityOrderByCreatedAtDesc(String entity, Pageable pageable);

   @Query("SELECT a FROM AuditLog a WHERE (CAST(:actorId AS string) IS NULL OR a.actorId = :actorId) AND (CAST(:entity AS string) IS NULL OR a.entity = :entity) AND (CAST(:from AS timestamp) IS NULL OR a.createdAt >= :from) AND (CAST(:to AS timestamp) IS NULL OR a.createdAt <= :to) ORDER BY a.createdAt DESC")
   Page<AuditLog> queryWithFilters(@Param("actorId") String actorId, @Param("entity") String entity, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}
