package com.coffee.app.service.impl;

import com.coffee.app.domain.Notification;
import com.coffee.app.dto.response.NotificationResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.mapper.NotificationMapper;
import com.coffee.app.repository.NotificationRepository;
import com.coffee.app.service.EmailNotificationService;
import com.coffee.app.service.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailNotificationService emailNotificationService;

    @Value("${notifications.admin-email:}")
    private String adminNotificationEmail;

    @Override
    public NotificationResponse createNotification(
            String type,
            String title,
            String message,
            String priority,
            UUID adminId,
            UUID relatedOrderId
    ) {
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .priority(priority)
                .adminId(adminId)
                .relatedOrderId(relatedOrderId)
                .read(false)
                .build();

        notification = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toResponse(notification);
        
        // Send email notification asynchronously (non-blocking)
        sendEmailNotificationAsync(response, type);
        
        return response;
    }
    
    private void sendEmailNotificationAsync(NotificationResponse notification, String type) {
        if (adminNotificationEmail == null || adminNotificationEmail.isBlank()) {
            log.warn("Admin notification email is not configured - skipping {} email", type);
            return;
        }

        try {
            switch(type.toLowerCase()) {
                case "order":
                    emailNotificationService.sendOrderNotification(notification, adminNotificationEmail);
                    break;
                case "payment":
                    emailNotificationService.sendPaymentNotification(notification, adminNotificationEmail);
                    break;
                case "stock":
                    emailNotificationService.sendStockAlertNotification(notification, adminNotificationEmail);
                    break;
                case "loyalty":
                    emailNotificationService.sendLoyaltyNotification(notification, adminNotificationEmail);
                    break;
                default:
                    emailNotificationService.sendGenericNotification(notification, adminNotificationEmail);
            }
        } catch (Exception e) {
            log.warn("Failed to send email notification: {}", e.getMessage());
            // Continue processing even if email fails - notifications are still stored in DB
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAllNotDeleted(pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotifications(Pageable pageable) {
        return notificationRepository.findUnread(pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByType(String type, Pageable pageable) {
        return notificationRepository.findByType(type, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByPriority(String priority, Pageable pageable) {
        return notificationRepository.findByPriority(priority, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countUnread();
    }

    @Override
    public NotificationResponse markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationResponse markAsUnread(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(false);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        notificationRepository.findAllUnread().forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        return notificationMapper.toResponse(notification);
    }
}

