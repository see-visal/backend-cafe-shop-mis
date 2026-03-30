package com.coffee.app.mapper;

import com.coffee.app.domain.Payment;
import com.coffee.app.dto.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
   componentModel = "spring"
)
public interface PaymentMapper {
   @Mapping(
      target = "orderId",
      source = "order.id"
   )
   PaymentResponse toResponse(Payment payment);
}
