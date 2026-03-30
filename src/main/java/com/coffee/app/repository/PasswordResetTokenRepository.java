package com.coffee.app.repository;

import com.coffee.app.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByOtpAndUsedFalse(String otp);
    void deleteByEmail(String email);
}
