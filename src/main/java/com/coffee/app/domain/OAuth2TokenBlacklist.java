package com.coffee.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_token_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2TokenBlacklist {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String tokenValue;

    @Column(nullable = false)
    private String tokenType; // "access_token" or "refresh_token"

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        revokedAt = LocalDateTime.now();
    }
}

