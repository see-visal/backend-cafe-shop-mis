package com.coffee.app.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coffee.app.domain.Order;
import com.coffee.app.domain.OrderItem;
import com.coffee.app.dto.response.OrderItemResponse;
import com.coffee.app.dto.response.OrderResponse;

@Component
public class OrderMapperImpl implements OrderMapper {
   @Autowired
   private OrderItemMapper orderItemMapper;

   public OrderResponse toResponse(Order order) {
      if (order == null) {
         return null;
      } else {
         List<OrderItemResponse> items = null;
         String notes = null;
         Integer estimatedMinutes = null;
         String clientSecret = null;
         String pickupToken = null;
         BigDecimal discountAmount = null;
         String promoCode = null;
         UUID id = null;
         UUID userId = null;
         String status = null;
         String orderType = null;
         Integer tableNumber = null;
         BigDecimal totalPrice = null;
         String paymentRef = null;
         UUID baristaId = null;
         LocalDateTime createdAt = null;
         LocalDateTime servedAt = null;
         items = this.orderItemListToOrderItemResponseList(order.getItems());
         notes = order.getNotes();
         estimatedMinutes = this.calcEstimated(order);
         clientSecret = order.getClientSecret();
         pickupToken = order.getPickupToken();
         discountAmount = order.getDiscountAmount();
         promoCode = order.getPromoCode();
         id = order.getId();
         userId = order.getUserId();
         status = order.getStatus();
         orderType = order.getOrderType();
         tableNumber = order.getTableNumber();
         totalPrice = order.getTotalPrice();
         paymentRef = order.getPaymentRef();
         baristaId = order.getBaristaId();
         createdAt = order.getCreatedAt();
         servedAt = order.getServedAt();
         OrderResponse orderResponse = new OrderResponse(id, userId, status, orderType, tableNumber, totalPrice, paymentRef, notes, baristaId, estimatedMinutes, createdAt, servedAt, items, clientSecret, pickupToken, discountAmount, promoCode);
         return orderResponse;
      }
   }

   protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
      if (list == null) {
         return null;
      } else {
         List<OrderItemResponse> list1 = new ArrayList<>(list.size());

         for(OrderItem orderItem : list) {
            list1.add(this.orderItemMapper.toResponse(orderItem));
         }

         return list1;
      }
   }
}
