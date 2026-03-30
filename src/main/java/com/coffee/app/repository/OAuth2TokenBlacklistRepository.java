package com.coffee.app.repository;

import com.coffee.app.domain.OAuth2TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OAuth2TokenBlacklistRepository extends JpaRepository<OAuth2TokenBlacklist, String> {
    Optional<OAuth2TokenBlacklist> findByTokenValue(String tokenValue);
    
    @Query("DELETE FROM OAuth2TokenBlacklist t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}

