package com.coffee.app.repository;

import com.coffee.app.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findAllNotDeleted(Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.read = false AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findUnread(Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findByType(@Param("type") String type, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.priority = :priority AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    Page<Notification> findByPriority(@Param("priority") String priority, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.read = false AND n.deletedAt IS NULL")
    List<Notification> findAllUnread();

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.read = false AND n.deletedAt IS NULL")
    long countUnread();

    @Query("SELECT n FROM Notification n WHERE n.relatedOrderId = :orderId AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findByRelatedOrderId(@Param("orderId") UUID orderId);

    @Query("SELECT n FROM Notification n WHERE n.deletedAt IS NULL AND n.createdAt > :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);
}

