package com.coffee.app.service;

import com.coffee.app.dto.request.OrderRequest;
import com.coffee.app.dto.response.OrderResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
   OrderResponse placeOrder(OrderRequest request);

   OrderResponse getOrder(UUID id);

   Page<OrderResponse> getAllOrders(Pageable pageable, String status, LocalDateTime from, LocalDateTime to);

   List<OrderResponse> getOrdersByUser(UUID userId);

   Page<OrderResponse> getOrdersByUserPaginated(UUID userId, String status, Pageable pageable);

   List<OrderResponse> getBaristaQueue();

   OrderResponse updateStatus(UUID id, String status, UUID baristaId);

   void cancelOrder(UUID id);

   OrderResponse confirmPickup(UUID orderId, String token, UUID baristaId);
}
