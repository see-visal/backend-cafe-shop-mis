package com.coffee.app.mapper;

import com.coffee.app.domain.OrderItem;
import com.coffee.app.dto.response.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
   componentModel = "spring"
)
public interface OrderItemMapper {
   @Mappings({@Mapping(
   target = "productId",
   source = "product.id"
), @Mapping(
   target = "productName",
   source = "product.name"
), @Mapping(
   target = "productImageUrl",
   source = "product.imageUrl"
), @Mapping(
   target = "specialInstructions",
   source = "specialInstructions"
)})
   OrderItemResponse toResponse(OrderItem item);
}
