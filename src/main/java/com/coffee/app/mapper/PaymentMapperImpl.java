package com.coffee.app.mapper;

import com.coffee.app.domain.Order;
import com.coffee.app.domain.Payment;
import com.coffee.app.dto.response.PaymentResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapperImpl implements PaymentMapper {
   public PaymentResponse toResponse(Payment payment) {
      if (payment == null) {
         return null;
      } else {
         UUID orderId = null;
         UUID id = null;
         String paymentMethod = null;
         String status = null;
         BigDecimal amount = null;
         String transactionRef = null;
         LocalDateTime paidAt = null;
         LocalDateTime createdAt = null;
         orderId = this.paymentOrderId(payment);
         id = payment.getId();
         paymentMethod = payment.getPaymentMethod();
         status = payment.getStatus();
         amount = payment.getAmount();
         transactionRef = payment.getTransactionRef();
         paidAt = payment.getPaidAt();
         createdAt = payment.getCreatedAt();
         PaymentResponse paymentResponse = new PaymentResponse(id, orderId, paymentMethod, status, amount, transactionRef, paidAt, createdAt);
         return paymentResponse;
      }
   }

   private UUID paymentOrderId(Payment payment) {
      Order order = payment.getOrder();
      return order == null ? null : order.getId();
   }
}
