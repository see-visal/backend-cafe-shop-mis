package com.coffee.app.mapper;

import com.coffee.app.domain.Order;
import com.coffee.app.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(
   componentModel = "spring",
   uses = {OrderItemMapper.class}
)
public interface OrderMapper {
   @Mappings({@Mapping(
   target = "items",
   source = "items"
), @Mapping(
   target = "notes",
   source = "notes"
), @Mapping(
   target = "estimatedMinutes",
   source = ".",
   qualifiedByName = {"calcEstimated"}
), @Mapping(
   target = "clientSecret",
   source = "clientSecret"
), @Mapping(
   target = "pickupToken",
   source = "pickupToken"
), @Mapping(
   target = "discountAmount",
   source = "discountAmount"
), @Mapping(
   target = "promoCode",
   source = "promoCode"
)})
   OrderResponse toResponse(Order order);

   @Named("calcEstimated")
   default Integer calcEstimated(Order order) {
      byte var10000;
      switch (order.getStatus()) {
         case "CONFIRMED" -> var10000 = 5;
         case "PREPARING" -> var10000 = 3;
         default -> var10000 = 0;
      }

      int base = var10000;
      if (base == 0) {
         return null;
      } else {
         int extra = (int)Math.ceil((double)order.getItems().size() / 3.0);
         return base + extra;
      }
   }
}
