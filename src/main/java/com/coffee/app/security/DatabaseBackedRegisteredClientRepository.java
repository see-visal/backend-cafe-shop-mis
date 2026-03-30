package com.coffee.app.security;

import com.coffee.app.domain.OAuth2RegisteredClient;
import com.coffee.app.repository.OAuth2RegisteredClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Database-backed RegisteredClientRepository
 * Loads OAuth2 clients from database instead of in-memory
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseBackedRegisteredClientRepository implements RegisteredClientRepository {

    private final OAuth2RegisteredClientRepository clientRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        log.debug("[DatabaseBackedRegisteredClientRepository] Saving registered client: {}", registeredClient.getClientId());
        // Convert RegisteredClient to OAuth2RegisteredClient entity and save
        // This would be implemented based on your needs
    }

    @Override
    public RegisteredClient findById(String id) {
        log.debug("[DatabaseBackedRegisteredClientRepository] Finding client by ID: {}", id);
        return clientRepository.findById(id)
                .map(this::mapToRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.debug("[DatabaseBackedRegisteredClientRepository] Finding client by clientId: {}", clientId);
        return clientRepository.findByClientId(clientId)
                .map(this::mapToRegisteredClient)
                .orElse(null);
    }

    /**
     * Maps OAuth2RegisteredClient entity to RegisteredClient
     */
    private RegisteredClient mapToRegisteredClient(OAuth2RegisteredClient entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientName(entity.getClientName());

        // Parse and add client authentication methods
        if (entity.getClientAuthenticationMethods() != null) {
            for (String method : entity.getClientAuthenticationMethods().split(",")) {
                builder.clientAuthenticationMethod(new ClientAuthenticationMethod(method.trim()));
            }
        }

        // Parse and add authorization grant types
        if (entity.getAuthorizationGrantTypes() != null) {
            for (String grantType : entity.getAuthorizationGrantTypes().split(",")) {
                builder.authorizationGrantType(new AuthorizationGrantType(grantType.trim()));
            }
        }

        // Parse and add redirect URIs
        if (entity.getRedirectUris() != null) {
            for (String uri : entity.getRedirectUris().split(",")) {
                builder.redirectUri(uri.trim());
            }
        }

        // Parse and add post logout redirect URIs
        if (entity.getPostLogoutRedirectUris() != null) {
            for (String uri : entity.getPostLogoutRedirectUris().split(",")) {
                builder.postLogoutRedirectUri(uri.trim());
            }
        }

        // Parse and add scopes
        if (entity.getScopes() != null) {
            for (String scope : entity.getScopes().split(",")) {
                builder.scope(scope.trim());
            }
        }

        return builder.build();
    }
}

