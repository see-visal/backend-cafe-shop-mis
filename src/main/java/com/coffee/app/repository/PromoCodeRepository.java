package com.coffee.app.repository;

import com.coffee.app.domain.PromoCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {
   Optional<PromoCode> findByCodeIgnoreCase(String code);
}
