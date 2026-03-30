package com.coffee.app.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Placeholder for OAuth2 Authorization Service
 * Not used in current OAuth2 implementation (based on JWT tokens)
 * Can be extended in future for full Spring Authorization Server integration
 */
@Slf4j
@Component
public class InMemoryOAuth2AuthorizationService {

    public InMemoryOAuth2AuthorizationService() {
        log.debug("[InMemoryOAuth2AuthorizationService] Initialized");
    }
}

