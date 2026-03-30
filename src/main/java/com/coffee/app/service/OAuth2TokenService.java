package com.coffee.app.service;

import com.coffee.app.domain.OAuth2Consent;
import com.coffee.app.domain.OAuth2RegisteredClient;
import com.coffee.app.domain.OAuth2TokenBlacklist;
import com.coffee.app.repository.OAuth2ConsentRepository;
import com.coffee.app.repository.OAuth2RegisteredClientRepository;
import com.coffee.app.repository.OAuth2TokenBlacklistRepository;
import com.coffee.app.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenService {

    private final JwtEncoder jwtEncoder;
    private final OAuth2RegisteredClientRepository clientRepository;
    private final OAuth2ConsentRepository consentRepository;
    private final OAuth2TokenBlacklistRepository blacklistRepository;
    private final PasswordEncoder passwordEncoder;

    private static final long ACCESS_TOKEN_MINUTES = 15L;
    private static final long REFRESH_TOKEN_DAYS = 7L;

    /**
     * Generate OAuth2 token pair for authenticated user
     */
    @Transactional
    public OAuth2TokenResponse generateTokenPair(CustomUserDetails user, String clientId, Set<String> grantedScopes) {
        log.debug("[OAuth2TokenService] Generating token pair for user: {} with client: {}", user.getUsername(), clientId);

        // Store user consent
        OAuth2Consent consent = OAuth2Consent.builder()
                .id(UUID.randomUUID().toString())
                .userUuid(user.getUuid())
                .clientId(clientId)
                .scopes(String.join(",", grantedScopes))
                .build();
        consentRepository.save(consent);

        Instant now = Instant.now();

        // Build access token claims
        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_MINUTES, ChronoUnit.MINUTES))
                .claim("uuid", user.getUuid())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .claim("scopes", grantedScopes)
                .claim("client_id", clientId)
                .claim("token_type", "Bearer")
                .build();

        // Build refresh token claims
        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS))
                .claim("uuid", user.getUuid())
                .claim("client_id", clientId)
                .claim("token_type", "Refresh")
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        log.debug("[OAuth2TokenService] Token pair generated successfully");

        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_MINUTES * 60) // in seconds
                .scope(String.join(" ", grantedScopes))
                .build();
    }

    /**
     * Generate token pair for client credentials flow (server-to-server)
     */
    public OAuth2TokenResponse generateClientCredentialsToken(String clientId, Set<String> grantedScopes) {
        log.debug("[OAuth2TokenService] Generating client credentials token for client: {}", clientId);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(clientId)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_MINUTES, ChronoUnit.MINUTES))
                .claim("client_id", clientId)
                .claim("scopes", grantedScopes)
                .claim("token_type", "Bearer")
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_MINUTES * 60)
                .scope(String.join(" ", grantedScopes))
                .build();
    }

    /**
     * Revoke a token by adding it to blacklist
     */
    @Transactional
    public void revokeToken(String token, String tokenType) {
        log.debug("[OAuth2TokenService] Revoking token of type: {}", tokenType);

        OAuth2TokenBlacklist blacklistEntry = OAuth2TokenBlacklist.builder()
                .id(UUID.randomUUID().toString())
                .tokenValue(token)
                .tokenType(tokenType)
                .expiresAt(LocalDateTime.now().plus(7, ChronoUnit.DAYS))
                .build();

        blacklistRepository.save(blacklistEntry);
        log.debug("[OAuth2TokenService] Token revoked successfully");
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistRepository.findByTokenValue(token).isPresent();
    }

    /**
     * Register new OAuth2 client
     */
    @Transactional
    public OAuth2ClientResponse registerClient(OAuth2ClientRequest request) {
        log.debug("[OAuth2TokenService] Registering new OAuth2 client: {}", request.getClientName());

        // Generate client credentials
        String clientId = UUID.randomUUID().toString();
        String clientSecret = generateClientSecret();

        OAuth2RegisteredClient client = OAuth2RegisteredClient.builder()
                .id(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientName(request.getClientName())
                .clientAuthenticationMethods(String.join(",", request.getClientAuthenticationMethods()))
                .authorizationGrantTypes(String.join(",", request.getAuthorizationGrantTypes()))
                .redirectUris(String.join(",", request.getRedirectUris()))
                .postLogoutRedirectUris(String.join(",", request.getPostLogoutRedirectUris()))
                .scopes(String.join(",", request.getScopes()))
                .build();

        clientRepository.save(client);
        log.debug("[OAuth2TokenService] Client registered with ID: {}", clientId);

        return OAuth2ClientResponse.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientName(request.getClientName())
                .build();
    }

    /**
     * Get user consents
     */
    public List<OAuth2Consent> getUserConsents(String userUuid) {
        return consentRepository.findByUserUuid(userUuid);
    }

    /**
     * Revoke user consent for a client
     */
    @Transactional
    public void revokeConsent(String userUuid, String clientId) {
        consentRepository.findByUserUuidAndClientId(userUuid, clientId)
                .ifPresent(consentRepository::delete);
    }

    /**
     * Generate secure random client secret
     */
    private String generateClientSecret() {
        return UUID.randomUUID() + "-" + UUID.randomUUID();
    }

    // ============= DTOs =============

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuth2TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn; // in seconds
        private String scope;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuth2ClientResponse {
        private String clientId;
        private String clientSecret;
        private String clientName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2ClientRequest {
        private String clientName;
        private Set<String> clientAuthenticationMethods;
        private Set<String> authorizationGrantTypes;
        private Set<String> redirectUris;
        private Set<String> postLogoutRedirectUris;
        private Set<String> scopes;
    }
}

