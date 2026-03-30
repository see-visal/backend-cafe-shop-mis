package com.coffee.app.controller;

import com.coffee.app.dto.request.OAuth2ClientRegistrationRequest;
import com.coffee.app.dto.response.OAuth2ClientResponse;
import com.coffee.app.service.OAuth2TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OAuth2", description = "OAuth2 token and client management endpoints")
public class OAuth2Controller {

    private final OAuth2TokenService oauth2TokenService;

    /**
     * Revoke token endpoint
     * Adds token to blacklist to prevent further use
     */
    @PostMapping("/revoke")
    @Operation(
        summary = "Revoke token",
        description = "Add a token to blacklist for revocation. Token will no longer be valid.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token revoked successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token parameter"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing bearer token")
    })
    public ResponseEntity<Map<String, String>> revokeToken(
            @RequestParam String token,
            @RequestParam(defaultValue = "access_token") String tokenType) {
        log.info("[OAuth2Controller] Revoke token request for type: {}", tokenType);

        oauth2TokenService.revokeToken(token, tokenType);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Token revoked successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Token introspection endpoint
     * Check if token is valid and not blacklisted
     */
    @PostMapping("/introspect")
    @Operation(
        summary = "Introspect token",
        description = "Check if token is valid and not revoked. Public endpoint, no authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token status returned"),
        @ApiResponse(responseCode = "400", description = "Invalid token parameter")
    })
    public ResponseEntity<Map<String, Object>> introspectToken(@RequestParam String token) {
        log.info("[OAuth2Controller] Token introspection request");

        boolean isBlacklisted = oauth2TokenService.isTokenBlacklisted(token);

        Map<String, Object> response = new HashMap<>();
        response.put("active", !isBlacklisted);
        response.put("blacklisted", isBlacklisted);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin endpoint: Register new OAuth2 client
     */
    @PostMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Register OAuth2 client",
        description = "Create new OAuth2 client for third-party applications. Admin only.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
    })
    public ResponseEntity<OAuth2ClientResponse> registerClient(
            @Valid @RequestBody(required = true) OAuth2ClientRegistrationRequest request) {
        log.info("[OAuth2Controller] Register OAuth2 client: {}", request.getClientName());

        OAuth2TokenService.OAuth2ClientResponse response = oauth2TokenService.registerClient(
                mapToServiceRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(response));
    }

    /**
     * Admin endpoint: List all registered clients
     */
    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List OAuth2 clients",
        description = "Get all registered OAuth2 clients. Admin only.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clients list returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
    })
    public ResponseEntity<Map<String, Object>> listClients() {
        log.info("[OAuth2Controller] List OAuth2 clients request");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Clients endpoint - feature in development");
        return ResponseEntity.ok(response);
    }

    // ============= Helper Methods =============

    private OAuth2TokenService.OAuth2ClientRequest mapToServiceRequest(OAuth2ClientRegistrationRequest dto) {
        OAuth2TokenService.OAuth2ClientRequest request = new OAuth2TokenService.OAuth2ClientRequest();

        request.setClientName(dto.getClientName());
        request.setClientAuthenticationMethods(dto.getClientAuthenticationMethods());
        request.setAuthorizationGrantTypes(dto.getAuthorizationGrantTypes());
        request.setRedirectUris(dto.getRedirectUris());
        request.setPostLogoutRedirectUris(dto.getPostLogoutRedirectUris());
        request.setScopes(dto.getScopes());
        return request;
    }

    private OAuth2ClientResponse mapToDto(OAuth2TokenService.OAuth2ClientResponse serviceResponse) {
        return new OAuth2ClientResponse(
                serviceResponse.getClientId(),
                serviceResponse.getClientSecret(),
                serviceResponse.getClientName(),
                java.time.LocalDateTime.now().toString()
        );
    }
}

