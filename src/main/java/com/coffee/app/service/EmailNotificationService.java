package com.coffee.app.service;

import com.coffee.app.dto.response.NotificationResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EmailNotificationService {
    void sendOrderNotification(NotificationResponse notification, String adminEmail);
    
    void sendPaymentNotification(NotificationResponse notification, String adminEmail);
    
    void sendStockAlertNotification(NotificationResponse notification, String adminEmail);
    
    void sendLoyaltyNotification(NotificationResponse notification, String adminEmail);
    
    void sendGenericNotification(NotificationResponse notification, String adminEmail);

    void sendCustomerCheckoutSuccessEmail(String customerEmail, String customerName, String orderId, BigDecimal amount, String paymentMethod, LocalDateTime paidAt);
}

