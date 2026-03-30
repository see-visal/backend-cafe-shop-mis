package com.coffee.app.mapper;

import com.coffee.app.domain.Notification;
import com.coffee.app.dto.response.NotificationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);

    Notification toEntity(NotificationResponse response);
}

