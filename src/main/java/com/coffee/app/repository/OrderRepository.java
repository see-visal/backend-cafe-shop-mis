package com.coffee.app.repository;

import com.coffee.app.domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {
   List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

   @Query("SELECT o FROM Order o WHERE o.userId = :userId AND (CAST(:status AS string) IS NULL OR o.status = :status) ORDER BY o.createdAt DESC")
   Page<Order> findByUserIdAndOptionalStatus(@Param("userId") UUID userId, @Param("status") String status, Pageable pageable);

   @Query("SELECT o FROM Order o WHERE o.status IN ('CONFIRMED','PREPARING','READY') ORDER BY o.createdAt ASC")
   List<Order> findBaristaQueue();

   @Query("SELECT o FROM Order o WHERE o.status = 'READY' AND o.createdAt <= :threshold")
   List<Order> findReadyBefore(@Param("threshold") LocalDateTime threshold);

   long countByStatus(String status);

   @Query("SELECT o FROM Order o WHERE (CAST(:status AS string) IS NULL OR o.status = :status) AND (CAST(:from AS timestamp) IS NULL OR o.createdAt >= :from) AND (CAST(:to AS timestamp) IS NULL OR o.createdAt <= :to) ORDER BY o.createdAt DESC")
   Page<Order> findWithFilters(@Param("status") String status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

   @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.createdAt <= :threshold")
   List<Order> findPendingBefore(@Param("threshold") LocalDateTime threshold);
}
