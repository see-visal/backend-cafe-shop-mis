package com.coffee.app.service;

import java.math.BigDecimal;

public interface TelegramNotificationService {
   void sendPaymentAlert(String paymentMethod, BigDecimal amount, String orderId);

   void sendCustomerCheckoutSuccess(String customerChatId, String customerName, String orderId, BigDecimal amount, String paymentMethod);
}
