package com.coffee.app.service.impl;

import com.coffee.app.domain.PromoCode;
import com.coffee.app.dto.request.PromoCodeRequest;
import com.coffee.app.dto.response.PromoCodeResponse;
import com.coffee.app.dto.response.PromoValidateResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.repository.PromoCodeRepository;
import com.coffee.app.service.PromoCodeService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {
   private final PromoCodeRepository promoCodeRepository;

   public List<PromoCodeResponse> getAll() {
      return this.promoCodeRepository.findAll().stream().map(this::toResponse).toList();
   }

   @Transactional
   public PromoCodeResponse create(PromoCodeRequest request) {
      PromoCode promo = PromoCode.builder().code(request.code().toUpperCase().trim()).discountType(request.discountType()).discountValue(request.discountValue()).maxUses(request.maxUses()).expiresAt(request.expiresAt()).build();
      return this.toResponse((PromoCode)this.promoCodeRepository.save(promo));
   }

   @Transactional
   public PromoCodeResponse expire(UUID id) {
      PromoCode promo = this.findOrThrow(id);
      promo.setActive(false);
      promo.setExpiresAt(LocalDateTime.now());
      return this.toResponse((PromoCode)this.promoCodeRepository.save(promo));
   }

   @Transactional
   public void delete(UUID id) {
      this.promoCodeRepository.delete(this.findOrThrow(id));
   }

   public PromoValidateResponse validate(String code, BigDecimal orderTotal) {
      PromoCode promo = (PromoCode)this.promoCodeRepository.findByCodeIgnoreCase(code).orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));
      if (!Boolean.TRUE.equals(promo.getActive())) {
         throw new IllegalArgumentException("Promo code is inactive: " + code);
      } else if (promo.getExpiresAt() != null && promo.getExpiresAt().isBefore(LocalDateTime.now())) {
         throw new IllegalArgumentException("Promo code has expired: " + code);
      } else if (promo.getMaxUses() != null && promo.getUsedCount() >= promo.getMaxUses()) {
         throw new IllegalArgumentException("Promo code has reached its maximum uses: " + code);
      } else {
         BigDecimal discount;
         if ("PERCENTAGE".equalsIgnoreCase(promo.getDiscountType())) {
            discount = orderTotal.multiply(promo.getDiscountValue()).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
         } else {
            discount = promo.getDiscountValue().min(orderTotal);
         }

         return new PromoValidateResponse(promo.getCode(), promo.getDiscountType(), promo.getDiscountValue(), discount, "Promo code applied successfully");
      }
   }

   private PromoCode findOrThrow(UUID id) {
      return (PromoCode)this.promoCodeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + String.valueOf(id)));
   }

   private PromoCodeResponse toResponse(PromoCode p) {
      return new PromoCodeResponse(p.getId(), p.getCode(), p.getDiscountType(), p.getDiscountValue(), p.getMaxUses(), p.getUsedCount(), p.getExpiresAt(), p.getActive(), p.getCreatedAt());
   }

   @Generated
   public PromoCodeServiceImpl(final PromoCodeRepository promoCodeRepository) {
      this.promoCodeRepository = promoCodeRepository;
   }
}
