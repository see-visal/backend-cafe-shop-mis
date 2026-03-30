package com.coffee.app.service.impl;

import com.coffee.app.dto.response.NotificationResponse;
import com.coffee.app.service.EmailNotificationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final JavaMailSender javaMailSender;
    private static final String FROM_EMAIL = "noreply@salseecoffee.com";
    private static final String ADMIN_SUBJECT_PREFIX = "[SalSee Admin Alert]";

    @Override
    public void sendOrderNotification(NotificationResponse notification, String adminEmail) {
        try {
            String subject = ADMIN_SUBJECT_PREFIX + " " + notification.title();
            String htmlContent = buildOrderEmailHtml(notification);
            sendHtmlEmail(adminEmail, subject, htmlContent);
            log.info("Order notification email sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send order notification email to {}: {}", adminEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendPaymentNotification(NotificationResponse notification, String adminEmail) {
        try {
            String subject = ADMIN_SUBJECT_PREFIX + " Payment Alert - " + notification.title();
            String htmlContent = buildPaymentEmailHtml(notification);
            sendHtmlEmail(adminEmail, subject, htmlContent);
            log.info("Payment notification email sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send payment notification email to {}: {}", adminEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendStockAlertNotification(NotificationResponse notification, String adminEmail) {
        try {
            String subject = ADMIN_SUBJECT_PREFIX + " URGENT - Stock Alert";
            String htmlContent = buildStockAlertEmailHtml(notification);
            sendHtmlEmail(adminEmail, subject, htmlContent);
            log.info("Stock alert email sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send stock alert email to {}: {}", adminEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendLoyaltyNotification(NotificationResponse notification, String adminEmail) {
        try {
            String subject = ADMIN_SUBJECT_PREFIX + " " + notification.title();
            String htmlContent = buildLoyaltyEmailHtml(notification);
            sendHtmlEmail(adminEmail, subject, htmlContent);
            log.info("Loyalty notification email sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send loyalty notification email to {}: {}", adminEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendGenericNotification(NotificationResponse notification, String adminEmail) {
        try {
            String subject = ADMIN_SUBJECT_PREFIX + " " + notification.title();
            String htmlContent = buildGenericEmailHtml(notification);
            sendHtmlEmail(adminEmail, subject, htmlContent);
            log.info("Generic notification email sent to {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send generic notification email to {}: {}", adminEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendCustomerCheckoutSuccessEmail(String customerEmail, String customerName, String orderId, BigDecimal amount, String paymentMethod, LocalDateTime paidAt) {
        try {
            String shortOrderId = orderId != null && orderId.length() >= 8 ? orderId.substring(0, 8).toUpperCase() : orderId;
            String subject = "[SalSee] Checkout Confirmed - Order #" + shortOrderId;
            String htmlContent = buildCustomerCheckoutEmailHtml(customerName, shortOrderId, amount, paymentMethod, paidAt);
            sendHtmlEmail(customerEmail, subject, htmlContent);
            log.info("Checkout success email sent to customer {}", customerEmail);
        } catch (Exception e) {
            log.error("Failed to send checkout success email to customer {}: {}", customerEmail, e.getMessage(), e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(FROM_EMAIL);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        javaMailSender.send(message);
    }

    private String buildOrderEmailHtml(NotificationResponse notification) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #d4a574 0%, #a0826d 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; }" +
            ".title { font-size: 20px; font-weight: bold; color: #333; margin-bottom: 16px; }" +
            ".message { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 16px; }" +
            ".priority { display: inline-block; padding: 6px 12px; border-radius: 4px; font-weight: bold; font-size: 12px; }" +
            ".priority.high { background-color: #fee2e2; color: #991b1b; }" +
            ".priority.medium { background-color: #fef3c7; color: #b45309; }" +
            ".priority.low { background-color: #f0fdf4; color: #15803d; }" +
            ".timestamp { font-size: 12px; color: #999; margin-top: 16px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>☕ SalSee Coffee Shop</h1><p>Order Alert</p></div>" +
            "<div class='content'>" +
            "<div class='title'>" + escapeHtml(notification.title()) + "</div>" +
            "<div class='message'>" + escapeHtml(notification.message()) + "</div>" +
            "<span class='priority " + notification.priority().toLowerCase() + "'>Priority: " + notification.priority().toUpperCase() + "</span>" +
            "<div class='timestamp'>" + formatDate(notification.createdAt().toString()) + "</div>" +
            "</div>" +
            "<div class='footer'>" +
            "<p>This is an automated notification from SalSee Admin System</p>" +
            "<p>Please log in to your dashboard at <a href='http://localhost:3001'>Admin Dashboard</a> for more details</p>" +
            "</div></div></body></html>";
    }

    private String buildPaymentEmailHtml(NotificationResponse notification) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; }" +
            ".title { font-size: 20px; font-weight: bold; color: #333; margin-bottom: 16px; }" +
            ".message { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 16px; background-color: #f0fdf4; padding: 12px; border-radius: 4px; border-left: 4px solid #10b981; }" +
            ".timestamp { font-size: 12px; color: #999; margin-top: 16px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>💳 Payment Confirmation</h1></div>" +
            "<div class='content'>" +
            "<div class='title'>" + escapeHtml(notification.title()) + "</div>" +
            "<div class='message'>" + escapeHtml(notification.message()) + "</div>" +
            "<div class='timestamp'>" + formatDate(notification.createdAt().toString()) + "</div>" +
            "</div>" +
            "<div class='footer'><p>This is an automated notification from SalSee Admin System</p></div>" +
            "</div></body></html>";
    }

    private String buildStockAlertEmailHtml(NotificationResponse notification) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; }" +
            ".title { font-size: 20px; font-weight: bold; color: #991b1b; margin-bottom: 16px; }" +
            ".message { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 16px; background-color: #fee2e2; padding: 12px; border-radius: 4px; border-left: 4px solid #ef4444; }" +
            ".timestamp { font-size: 12px; color: #999; margin-top: 16px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>⚠️ LOW STOCK ALERT</h1></div>" +
            "<div class='content'>" +
            "<div class='title'>" + escapeHtml(notification.title()) + "</div>" +
            "<div class='message'>" + escapeHtml(notification.message()) + "</div>" +
            "<div class='timestamp'>" + formatDate(notification.createdAt().toString()) + "</div>" +
            "</div>" +
            "<div class='footer'>" +
            "<p>This is an urgent automated notification from SalSee Admin System</p>" +
            "<p>Action Required: Please update inventory levels immediately</p>" +
            "</div></div></body></html>";
    }

    private String buildLoyaltyEmailHtml(NotificationResponse notification) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; }" +
            ".title { font-size: 20px; font-weight: bold; color: #333; margin-bottom: 16px; }" +
            ".message { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 16px; }" +
            ".timestamp { font-size: 12px; color: #999; margin-top: 16px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>⭐ Loyalty Program Alert</h1></div>" +
            "<div class='content'>" +
            "<div class='title'>" + escapeHtml(notification.title()) + "</div>" +
            "<div class='message'>" + escapeHtml(notification.message()) + "</div>" +
            "<div class='timestamp'>" + formatDate(notification.createdAt().toString()) + "</div>" +
            "</div>" +
            "<div class='footer'><p>This is an automated notification from SalSee Admin System</p></div>" +
            "</div></body></html>";
    }

    private String buildGenericEmailHtml(NotificationResponse notification) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; }" +
            ".title { font-size: 20px; font-weight: bold; color: #333; margin-bottom: 16px; }" +
            ".message { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 16px; }" +
            ".timestamp { font-size: 12px; color: #999; margin-top: 16px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>🔔 SalSee Notification</h1></div>" +
            "<div class='content'>" +
            "<div class='title'>" + escapeHtml(notification.title()) + "</div>" +
            "<div class='message'>" + escapeHtml(notification.message()) + "</div>" +
            "<div class='timestamp'>" + formatDate(notification.createdAt().toString()) + "</div>" +
            "</div>" +
            "<div class='footer'><p>This is an automated notification from SalSee Admin System</p></div>" +
            "</div></body></html>";
    }

    private String buildCustomerCheckoutEmailHtml(String customerName, String orderId, BigDecimal amount, String paymentMethod, LocalDateTime paidAt) {
        String safeName = escapeHtml(customerName == null || customerName.isBlank() ? "Customer" : customerName.trim());
        String safeOrderId = escapeHtml(orderId == null ? "-" : orderId);
        String safeAmount = amount == null ? "0.00" : amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
        String safeMethod = escapeHtml(paymentMethod == null ? "Unknown" : paymentMethod);
        String safePaidAt = paidAt == null ? "-" : escapeHtml(paidAt.toString().replace("T", " "));

        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: 'Segoe UI', sans-serif; background-color: #f5f5f5; }" +
            ".container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
            ".header { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; padding: 24px; text-align: center; border-radius: 8px 8px 0 0; }" +
            ".content { padding: 24px; color: #333; }" +
            ".title { font-size: 20px; font-weight: bold; margin-bottom: 16px; }" +
            ".line { margin: 8px 0; font-size: 14px; }" +
            ".footer { background-color: #f9f9f9; padding: 16px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0; }" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h1>Payment Successful</h1><p>Thank you for your order</p></div>" +
            "<div class='content'>" +
            "<div class='title'>Hi " + safeName + ", your checkout is confirmed.</div>" +
            "<div class='line'><b>Order:</b> #" + safeOrderId + "</div>" +
            "<div class='line'><b>Amount:</b> $" + safeAmount + "</div>" +
            "<div class='line'><b>Payment method:</b> " + safeMethod + "</div>" +
            "<div class='line'><b>Paid at:</b> " + safePaidAt + "</div>" +
            "</div>" +
            "<div class='footer'>This is an automated message from SalSee Coffee Shop.</div>" +
            "</div></body></html>";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private String formatDate(String timestamp) {
        try {
            return timestamp.substring(0, 19).replace("T", " at ");
        } catch (Exception e) {
            return timestamp;
        }
    }
}

