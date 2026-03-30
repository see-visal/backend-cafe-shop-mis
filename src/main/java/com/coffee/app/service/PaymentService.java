package com.coffee.app.service;

import java.util.UUID;

import com.coffee.app.dto.request.PaymentRequest;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.dto.response.ReceiptResponse;

public interface PaymentService {
   PaymentResponse createPayment(PaymentRequest request);

   PaymentResponse confirmPayment(UUID paymentId, String transactionRef);

   PaymentResponse voidOrRefundPayment(UUID paymentId, String reason);

   PaymentResponse getPaymentByOrder(UUID orderId);

   ReceiptResponse getReceipt(UUID orderId);
}
