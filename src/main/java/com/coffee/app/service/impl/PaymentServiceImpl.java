package com.coffee.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.controller.CustomerController;
import com.coffee.app.domain.Order;
import com.coffee.app.domain.Payment;
import com.coffee.app.domain.User;
import com.coffee.app.dto.request.PaymentRequest;
import com.coffee.app.dto.response.OrderResponse;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.dto.response.ReceiptResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.OrderMapper;
import com.coffee.app.mapper.PaymentMapper;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.repository.PaymentRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.EmailNotificationService;
import com.coffee.app.service.NotificationService;
import com.coffee.app.service.PaymentService;
import com.coffee.app.service.TelegramNotificationService;

import lombok.Generated;

@Service
public class PaymentServiceImpl implements PaymentService {
   private static final UUID DEFAULT_ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
   private final PaymentRepository paymentRepository;
   private final OrderRepository orderRepository;
   private final PaymentMapper paymentMapper;
   private final OrderMapper orderMapper;
   private final TelegramNotificationService telegramService;
   private final EmailNotificationService emailNotificationService;
   private final NotificationService notificationService;
   private final UserRepository userRepository;

   @Transactional
   public PaymentResponse createPayment(PaymentRequest request) {
      Order order = this.findOrderOrThrow(request.orderId());
      Optional<Payment> existing = this.paymentRepository.findByOrderId(order.getId());
      if (existing.isPresent() && this.isPaid((Payment)existing.get())) {
         Payment paid = (Payment)existing.get();
         String normalizedTransactionRef = this.normalizeTransactionRef(request.transactionRef());
         if (normalizedTransactionRef != null && (paid.getTransactionRef() == null || paid.getTransactionRef().isBlank())) {
            paid.setTransactionRef(normalizedTransactionRef);
            paid = (Payment)this.paymentRepository.save(paid);
         }

         return this.paymentMapper.toResponse(paid);
      } else if (this.isInstantPaymentMethod(request.paymentMethod())) {
         return this.recordSuccessfulPayment(order.getId(), request.paymentMethod(), request.amount(), request.transactionRef());
      } else {
         Payment payment = existing.orElseGet(() -> Payment.builder().order(order).build());
         payment.setPaymentMethod(request.paymentMethod());
         payment.setAmount(this.resolvePaymentAmount(order, request.amount()));
         payment.setStatus("PENDING");
         payment.setPaidAt((LocalDateTime)null);
         payment.setTransactionRef(this.normalizeTransactionRef(request.transactionRef()));
         Payment saved = (Payment)this.paymentRepository.save(payment);
         return this.paymentMapper.toResponse(saved);
      }
   }

   @Transactional
   public PaymentResponse recordSuccessfulPayment(UUID orderId, String paymentMethod, BigDecimal amount, String transactionRef) {
      Order order = this.findOrderOrThrow(orderId);
      Optional<Payment> existing = this.paymentRepository.findByOrderId(orderId);
      Payment payment = existing.orElseGet(() -> Payment.builder().order(order).build());
      boolean wasPaid = this.isPaid(payment);
      payment.setPaymentMethod(paymentMethod);
      payment.setAmount(this.resolvePaymentAmount(order, amount));
      payment.setStatus("PAID");
      if (!wasPaid || payment.getPaidAt() == null) {
         payment.setPaidAt(LocalDateTime.now());
      }

      String normalizedTransactionRef = this.normalizeTransactionRef(transactionRef);
      if (normalizedTransactionRef != null) {
         payment.setTransactionRef(normalizedTransactionRef);
      }

      Payment saved = (Payment)this.paymentRepository.save(payment);
      Order syncedOrder = this.syncOrderAfterPayment(order, saved);
      if (!wasPaid) {
         this.publishSuccessfulPayment(syncedOrder, saved);
      }

      return this.paymentMapper.toResponse(saved);
   }

   @Transactional
   public PaymentResponse confirmPayment(UUID paymentId, String transactionRef) {
      Payment payment = (Payment)this.paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + String.valueOf(paymentId)));
      return this.recordSuccessfulPayment(payment.getOrder().getId(), payment.getPaymentMethod(), payment.getAmount(), transactionRef);
   }

   private Order syncOrderAfterPayment(Order order, Payment payment) {
      boolean changed = false;
      if ("PENDING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
         order.setStatus("CONFIRMED");
         changed = true;
      }

      String paymentRef = payment.getId() != null ? payment.getId().toString() : null;
      if (paymentRef != null && (order.getPaymentRef() == null || order.getPaymentRef().isBlank())) {
         order.setPaymentRef(paymentRef);
         changed = true;
      }

      if (!changed) {
         return order;
      }

      Order savedOrder = (Order)this.orderRepository.save(order);
      OrderResponse response = this.orderMapper.toResponse(savedOrder);
      CustomerController.pushOrderUpdate(savedOrder.getId().toString(), response);
      return savedOrder;
   }

   private void publishSuccessfulPayment(Order order, Payment payment) {
      this.notificationService.createNotification("payment", "Payment received for order #" + this.shortOrderId(order.getId()), this.buildAdminPaymentMessage(order, payment), "medium", DEFAULT_ADMIN_ID, order.getId());
      this.telegramService.sendPaymentAlert(payment.getPaymentMethod(), payment.getAmount(), order.getId().toString());
      this.notifyCustomerCheckoutSuccess(order, payment);
   }

   private String buildAdminPaymentMessage(Order order, Payment payment) {
      String paymentMethod = this.humanizePaymentMethod(payment.getPaymentMethod());
      String amount = this.formatAmount(payment.getAmount());
      return paymentMethod + " payment confirmed for order #" + this.shortOrderId(order.getId()) + ". Amount: $" + amount + ".";
   }

   private String humanizePaymentMethod(String paymentMethod) {
      if (paymentMethod == null || paymentMethod.isBlank()) {
         return "Unknown";
      }

      return paymentMethod.replace('_', ' ').trim();
   }

   private String formatAmount(BigDecimal amount) {
      BigDecimal normalizedAmount = amount != null ? amount : BigDecimal.ZERO;
      return normalizedAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
   }

   private String shortOrderId(UUID orderId) {
      String value = String.valueOf(orderId);
      return value.length() >= 8 ? value.substring(0, 8).toUpperCase() : value.toUpperCase();
   }

   private boolean isInstantPaymentMethod(String paymentMethod) {
      return "QR_CODE".equalsIgnoreCase(paymentMethod) || "CARD".equalsIgnoreCase(paymentMethod);
   }

   private boolean isPaid(Payment payment) {
      return payment.getStatus() != null && "PAID".equalsIgnoreCase(payment.getStatus());
   }

   private BigDecimal resolvePaymentAmount(Order order, BigDecimal requestedAmount) {
      if (order.getTotalPrice() != null) {
         return order.getTotalPrice();
      }

      return requestedAmount != null ? requestedAmount : BigDecimal.ZERO;
   }

   private String normalizeTransactionRef(String transactionRef) {
      if (transactionRef == null) {
         return null;
      }

      String trimmed = transactionRef.trim();
      return trimmed.isBlank() ? null : trimmed;
   }

   private Order findOrderOrThrow(UUID orderId) {
      return (Order)this.orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + String.valueOf(orderId)));
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
   public PaymentServiceImpl(final PaymentRepository paymentRepository, final OrderRepository orderRepository, final PaymentMapper paymentMapper, final OrderMapper orderMapper, final TelegramNotificationService telegramService, final EmailNotificationService emailNotificationService, final NotificationService notificationService, final UserRepository userRepository) {
      this.paymentRepository = paymentRepository;
      this.orderRepository = orderRepository;
      this.paymentMapper = paymentMapper;
      this.orderMapper = orderMapper;
      this.telegramService = telegramService;
      this.emailNotificationService = emailNotificationService;
      this.notificationService = notificationService;
      this.userRepository = userRepository;
   }
}
