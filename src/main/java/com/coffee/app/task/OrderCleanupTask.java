package com.coffee.app.task;

import com.coffee.app.domain.Order;
import com.coffee.app.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCleanupTask {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(OrderCleanupTask.class);
   private final OrderRepository orderRepository;

   @Scheduled(
      cron = "0 0 2 * * ?"
   )
   public void cleanupGhostOrders() {
      log.info("Running daily cleanup task for ghost orders...");
      LocalDateTime threshold = LocalDateTime.now().minusHours(24L);
      List<Order> pendingOrders = this.orderRepository.findPendingBefore(threshold);
      int count = 0;

      for(Order order : pendingOrders) {
         order.setStatus("EXPIRED");
         this.orderRepository.save(order);
         ++count;
      }

      log.info("Cleanup completed. Expired {} ghost order(s).", count);
   }

   @Generated
   public OrderCleanupTask(final OrderRepository orderRepository) {
      this.orderRepository = orderRepository;
   }
}
