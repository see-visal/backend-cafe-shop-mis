package com.coffee.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Generated;

@Entity
@Table(
   name = "audit_logs",
   indexes = {@Index(
   name = "idx_audit_actor",
   columnList = "actor_id"
), @Index(
   name = "idx_audit_entity",
   columnList = "entity, entity_id"
), @Index(
   name = "idx_audit_ts",
   columnList = "created_at"
)}
)
public class AuditLog {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   private Long id;
   @Column(
      name = "actor_id",
      nullable = false,
      length = 64
   )
   private String actorId;
   @Column(
      nullable = false,
      length = 80
   )
   private String action;
   @Column(
      nullable = false,
      length = 80
   )
   private String entity;
   @Column(
      name = "entity_id",
      length = 64
   )
   private String entityId;
   @Column(
      columnDefinition = "TEXT"
   )
   private String detail;
   @Column(
      name = "created_at",
      updatable = false,
      nullable = false
   )
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }

   @Generated
   public static AuditLogBuilder builder() {
      return new AuditLogBuilder();
   }

   @Generated
   public Long getId() {
      return this.id;
   }

   @Generated
   public String getActorId() {
      return this.actorId;
   }

   @Generated
   public String getAction() {
      return this.action;
   }

   @Generated
   public String getEntity() {
      return this.entity;
   }

   @Generated
   public String getEntityId() {
      return this.entityId;
   }

   @Generated
   public String getDetail() {
      return this.detail;
   }

   @Generated
   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   @Generated
   public void setId(final Long id) {
      this.id = id;
   }

   @Generated
   public void setActorId(final String actorId) {
      this.actorId = actorId;
   }

   @Generated
   public void setAction(final String action) {
      this.action = action;
   }

   @Generated
   public void setEntity(final String entity) {
      this.entity = entity;
   }

   @Generated
   public void setEntityId(final String entityId) {
      this.entityId = entityId;
   }

   @Generated
   public void setDetail(final String detail) {
      this.detail = detail;
   }

   @Generated
   public void setCreatedAt(final LocalDateTime createdAt) {
      this.createdAt = createdAt;
   }

   @Generated
   public AuditLog() {
   }

   @Generated
   public AuditLog(final Long id, final String actorId, final String action, final String entity, final String entityId, final String detail, final LocalDateTime createdAt) {
      this.id = id;
      this.actorId = actorId;
      this.action = action;
      this.entity = entity;
      this.entityId = entityId;
      this.detail = detail;
      this.createdAt = createdAt;
   }

   @Generated
   public static class AuditLogBuilder {
      @Generated
      private Long id;
      @Generated
      private String actorId;
      @Generated
      private String action;
      @Generated
      private String entity;
      @Generated
      private String entityId;
      @Generated
      private String detail;
      @Generated
      private LocalDateTime createdAt;

      @Generated
      AuditLogBuilder() {
      }

      @Generated
      public AuditLogBuilder id(final Long id) {
         this.id = id;
         return this;
      }

      @Generated
      public AuditLogBuilder actorId(final String actorId) {
         this.actorId = actorId;
         return this;
      }

      @Generated
      public AuditLogBuilder action(final String action) {
         this.action = action;
         return this;
      }

      @Generated
      public AuditLogBuilder entity(final String entity) {
         this.entity = entity;
         return this;
      }

      @Generated
      public AuditLogBuilder entityId(final String entityId) {
         this.entityId = entityId;
         return this;
      }

      @Generated
      public AuditLogBuilder detail(final String detail) {
         this.detail = detail;
         return this;
      }

      @Generated
      public AuditLogBuilder createdAt(final LocalDateTime createdAt) {
         this.createdAt = createdAt;
         return this;
      }

      @Generated
      public AuditLog build() {
         return new AuditLog(this.id, this.actorId, this.action, this.entity, this.entityId, this.detail, this.createdAt);
      }

      @Generated
      public String toString() {
         Long var10000 = this.id;
         return "AuditLog.AuditLogBuilder(id=" + var10000 + ", actorId=" + this.actorId + ", action=" + this.action + ", entity=" + this.entity + ", entityId=" + this.entityId + ", detail=" + this.detail + ", createdAt=" + String.valueOf(this.createdAt) + ")";
      }
   }
}
