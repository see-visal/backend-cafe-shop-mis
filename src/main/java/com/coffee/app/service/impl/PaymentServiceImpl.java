package com.coffee.app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.Order;
import com.coffee.app.domain.Payment;
import com.coffee.app.domain.User;
import com.coffee.app.dto.request.PaymentRequest;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.dto.response.ReceiptResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.PaymentMapper;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.repository.PaymentRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.EmailNotificationService;
import com.coffee.app.service.PaymentService;
import com.coffee.app.service.TelegramNotificationService;

import lombok.Generated;

@Service
public class PaymentServiceImpl implements PaymentService {
   private final PaymentRepository paymentRepository;
   private final OrderRepository orderRepository;
   private final PaymentMapper paymentMapper;
   private final TelegramNotificationService telegramService;
   private final EmailNotificationService emailNotificationService;
   private final UserRepository userRepository;

   @Transactional
   public PaymentResponse createPayment(PaymentRequest request) {
      Order order = (Order)this.orderRepository.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + String.valueOf(request.orderId())));
      Optional<Payment> existing = this.paymentRepository.findByOrderId(request.orderId());
      if (existing.isPresent() && "PAID".equals(((Payment)existing.get()).getStatus())) {
         return this.paymentMapper.toResponse((Payment)existing.get());
      } else {
         boolean instant = "QR_CODE".equals(request.paymentMethod()) || "CARD".equals(request.paymentMethod());
         Payment payment = Payment.builder().order(order).paymentMethod(request.paymentMethod()).amount(request.amount()).transactionRef(request.transactionRef()).status(instant ? "PAID" : "PENDING").paidAt(instant ? LocalDateTime.now() : null).build();
         Payment saved = (Payment)this.paymentRepository.save(payment);
         if (instant && "PENDING_PAYMENT".equals(order.getStatus())) {
            order.setStatus("CONFIRMED");
            order.setPaymentRef(saved.getId().toString());
            this.orderRepository.save(order);
         }

         if (instant) {
            this.telegramService.sendPaymentAlert(saved.getPaymentMethod(), saved.getAmount(), order.getId().toString());
            this.notifyCustomerCheckoutSuccess(order, saved);
         }

         return this.paymentMapper.toResponse(saved);
      }
   }

   @Transactional
   public PaymentResponse confirmPayment(UUID paymentId, String transactionRef) {
      Payment payment = (Payment)this.paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + String.valueOf(paymentId)));
      boolean wasPaid = "PAID".equalsIgnoreCase(payment.getStatus());
      payment.setStatus("PAID");
      payment.setPaidAt(LocalDateTime.now());
      if (transactionRef != null) {
         payment.setTransactionRef(transactionRef);
      }

      Order order = payment.getOrder();
      if ("PENDING_PAYMENT".equals(order.getStatus())) {
         order.setStatus("CONFIRMED");
         order.setPaymentRef(payment.getId().toString());
         this.orderRepository.save(order);
      }

      Payment saved = (Payment)this.paymentRepository.save(payment);
      this.telegramService.sendPaymentAlert(saved.getPaymentMethod(), saved.getAmount(), order.getId().toString());
      if (!wasPaid) {
         this.notifyCustomerCheckoutSuccess(order, saved);
      }
      return this.paymentMapper.toResponse(saved);
   }

   private void notifyCustomerCheckoutSuccess(Order order, Payment payment) {
      if (order.getUserId() == null) {
         return;
      }

      this.userRepository.findByUuid(order.getUserId().toString()).ifPresent((user) -> {
         String fullName = this.resolveDisplayName(user);
         if (user.getEmail() != null && !user.getEmail().isBlank()) {
            this.emailNotificationService.sendCustomerCheckoutSuccessEmail(user.getEmail(), fullName, order.getId().toString(), payment.getAmount(), payment.getPaymentMethod(), payment.getPaidAt());
         }

         if (user.getTelegramChatId() != null && !user.getTelegramChatId().isBlank()) {
            this.telegramService.sendCustomerCheckoutSuccess(user.getTelegramChatId(), fullName, order.getId().toString(), payment.getAmount(), payment.getPaymentMethod());
         }
      });
   }

   private String resolveDisplayName(User user) {
      String givenName = user.getGivenName() != null ? user.getGivenName().trim() : "";
      String familyName = user.getFamilyName() != null ? user.getFamilyName().trim() : "";
      String fullName = (givenName + " " + familyName).trim();
      if (!fullName.isEmpty()) {
         return fullName;
      }

      return user.getUsername() != null && !user.getUsername().isBlank() ? user.getUsername() : "Customer";
   }

   @Transactional
   public PaymentResponse voidOrRefundPayment(UUID paymentId, String reason) {
      Payment payment = (Payment)this.paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + String.valueOf(paymentId)));
      String status = payment.getStatus() != null ? payment.getStatus().toUpperCase() : "PENDING";
      if ("VOID".equals(status) || "REFUNDED".equals(status)) {
         return this.paymentMapper.toResponse(payment);
      }

      Order order = payment.getOrder();
      if ("PAID".equals(status)) {
         payment.setStatus("REFUNDED");
      } else if ("PENDING".equals(status)) {
         payment.setStatus("VOID");
      } else {
         throw new IllegalArgumentException("Unsupported payment status for void/refund: " + status);
      }

      if (reason != null && !reason.isBlank()) {
         String trimmedReason = reason.trim();
         payment.setTransactionRef(payment.getTransactionRef() == null || payment.getTransactionRef().isBlank() ? trimmedReason : payment.getTransactionRef() + " | " + trimmedReason);
      }

      String orderStatus = order.getStatus() != null ? order.getStatus().toUpperCase() : "";
      if (!"SERVED".equals(orderStatus) && !"CANCELLED".equals(orderStatus)) {
         order.setStatus("CANCELLED");
         this.orderRepository.save(order);
      }

      Payment saved = (Payment)this.paymentRepository.save(payment);
      return this.paymentMapper.toResponse(saved);
   }

   @Transactional(
      readOnly = true
   )
   public PaymentResponse getPaymentByOrder(UUID orderId) {
      return this.paymentRepository.findByOrderId(orderId).map(this.paymentMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + String.valueOf(orderId)));
   }

   @Transactional(
      readOnly = true
   )
   public ReceiptResponse getReceipt(UUID orderId) {
      Order order = (Order)this.orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + String.valueOf(orderId)));
      Payment payment = (Payment)this.paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + String.valueOf(orderId)));
      List<ReceiptResponse.ReceiptItemResponse> items = order.getItems().stream().map((i) -> new ReceiptResponse.ReceiptItemResponse(i.getProduct().getName(), i.getQuantity(), i.getUnitPrice(), i.getUnitPrice().multiply(BigDecimal.valueOf((long)i.getQuantity())))).toList();
      return new ReceiptResponse(order.getId(), payment.getId(), order.getOrderType(), order.getTableNumber(), items, order.getTotalPrice(), payment.getAmount(), payment.getPaymentMethod(), payment.getPaidAt());
   }

   @Generated
   public PaymentServiceImpl(final PaymentRepository paymentRepository, final OrderRepository orderRepository, final PaymentMapper paymentMapper, final TelegramNotificationService telegramService, final EmailNotificationService emailNotificationService, final UserRepository userRepository) {
      this.paymentRepository = paymentRepository;
      this.orderRepository = orderRepository;
      this.paymentMapper = paymentMapper;
      this.telegramService = telegramService;
      this.emailNotificationService = emailNotificationService;
      this.userRepository = userRepository;
   }
}
