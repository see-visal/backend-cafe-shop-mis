package com.coffee.app.service;

import com.coffee.app.dto.request.PromoCodeRequest;
import com.coffee.app.dto.response.PromoCodeResponse;
import com.coffee.app.dto.response.PromoValidateResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PromoCodeService {
   List<PromoCodeResponse> getAll();

   PromoCodeResponse create(PromoCodeRequest request);

   PromoCodeResponse expire(UUID id);

   void delete(UUID id);

   PromoValidateResponse validate(String code, BigDecimal orderTotal);
}
