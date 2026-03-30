package com.coffee.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_registered_client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2RegisteredClient {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String clientId;

    @Column(name = "client_id_issued_at")
    private LocalDateTime clientIdIssuedAt;

    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private LocalDateTime clientSecretExpiresAt;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientAuthenticationMethods; // "client_secret_basic,client_secret_post"

    @Column(nullable = false)
    private String authorizationGrantTypes; // "client_credentials,authorization_code,refresh_token"

    private String redirectUris; // comma-separated

    private String postLogoutRedirectUris; // comma-separated

    @Column(nullable = false)
    private String scopes; // comma-separated

    private String clientSettings; // JSON

    private String tokenSettings; // JSON

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

