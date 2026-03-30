package com.coffee.app.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2 Configuration Placeholder
 * 
 * Note: All OAuth2 beans (JwtEncoder, JwtDecoder, RSAKey, JWKSource) are now defined in SecurityConfig.java
 * This file is kept for future extensions (e.g., OAuth2 authorization server endpoints)
 */
@Configuration
@Slf4j
public class OAuth2AuthorizationServerConfig {
    
    public OAuth2AuthorizationServerConfig() {
        log.info("[OAuth2AuthorizationServerConfig] OAuth2 beans are managed by SecurityConfig");
    }
}

