package com.coffee.app.mapper;

import com.coffee.app.domain.Notification;
import com.coffee.app.dto.response.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getPriority(),
                notification.getRead(),
                notification.getAdminId(),
                notification.getRelatedOrderId(),
                notification.getCreatedAt()
        );
    }

    @Override
    public Notification toEntity(NotificationResponse response) {
        if (response == null) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.id(response.id());
        notification.type(response.type());
        notification.title(response.title());
        notification.message(response.message());
        notification.priority(response.priority());
        notification.read(response.read());
        notification.adminId(response.adminId());
        notification.relatedOrderId(response.relatedOrderId());
        notification.createdAt(response.createdAt());

        return notification.build();
    }
}
