package com.coffee.app.service;

import com.coffee.app.dto.response.NotificationResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse createNotification(
            String type,
            String title,
            String message,
            String priority,
            UUID adminId,
            UUID relatedOrderId
    );

    Page<NotificationResponse> getAllNotifications(Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(Pageable pageable);

    Page<NotificationResponse> getNotificationsByType(String type, Pageable pageable);

    Page<NotificationResponse> getNotificationsByPriority(String priority, Pageable pageable);

    long getUnreadCount();

    NotificationResponse markAsRead(UUID notificationId);

    NotificationResponse markAsUnread(UUID notificationId);

    void deleteNotification(UUID notificationId);

    void markAllAsRead();

    NotificationResponse getNotification(UUID notificationId);
}

