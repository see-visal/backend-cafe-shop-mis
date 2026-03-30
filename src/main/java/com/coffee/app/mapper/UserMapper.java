package com.coffee.app.mapper;

import com.coffee.app.domain.User;
import com.coffee.app.dto.response.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
   componentModel = "spring"
)
public interface UserMapper {
   @Mappings({@Mapping(
   target = "roles",
   expression = "java(user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()))"
), @Mapping(
   target = "loyaltyPoints",
   expression = "java(user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0)"
), @Mapping(
   target = "notificationPreference",
   expression = "java(user.getNotificationPreference() != null ? user.getNotificationPreference() : \"IN_APP\")"
)})
   UserProfileResponse toProfileResponse(User user);
}
