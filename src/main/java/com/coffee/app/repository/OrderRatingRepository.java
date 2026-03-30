package com.coffee.app.repository;

import com.coffee.app.domain.OrderRating;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRatingRepository extends JpaRepository<OrderRating, UUID> {
   List<OrderRating> findByProductIdOrderByCreatedAtDesc(UUID productId);

   boolean existsByOrderIdAndUserId(UUID orderId, UUID userId);

   @Query("SELECT AVG(r.stars) FROM OrderRating r WHERE r.product.id = :productId")
   Double findAverageStarsByProductId(@Param("productId") UUID productId);
}
