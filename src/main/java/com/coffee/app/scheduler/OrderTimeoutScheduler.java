package com.coffee.app.scheduler;

import com.coffee.app.domain.Order;
import com.coffee.app.repository.OrderRepository;
import java.time.LocalDateTime;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderTimeoutScheduler {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(OrderTimeoutScheduler.class);
   private final OrderRepository orderRepository;

   @Scheduled(
      fixedRate = 300000L
   )
   @Transactional
   public void cancelUnpaidOrders() {
      LocalDateTime threshold = LocalDateTime.now().minusMinutes(15L);

      for(Order order : this.orderRepository.findPendingBefore(threshold)) {
         order.setStatus("CANCELLED");
         this.orderRepository.save(order);
         log.info("Auto-cancelled unpaid order {} (created at {})", order.getId(), order.getCreatedAt());
      }

   }

   @Scheduled(
      fixedRate = 300000L
   )
   @Transactional
   public void autoServeReadyOrders() {
      LocalDateTime threshold = LocalDateTime.now().minusMinutes(30L);

      for(Order order : this.orderRepository.findReadyBefore(threshold)) {
         order.setStatus("SERVED");
         order.setServedAt(LocalDateTime.now());
         order.setPickupToken((String)null);
         order.setPickupTokenExpiresAt((LocalDateTime)null);
         this.orderRepository.save(order);
         log.info("Auto-served READY order {} (ready since {})", order.getId(), order.getCreatedAt());
      }

   }

   @Generated
   public OrderTimeoutScheduler(final OrderRepository orderRepository) {
      this.orderRepository = orderRepository;
   }
}
