package com.coffee.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientRegistrationRequest {

    @NotEmpty(message = "Client name is required")
    @Schema(description = "Name of the OAuth2 client application", example = "Mobile App")
    private String clientName;

    @NotEmpty(message = "Authorization grant types are required")
    @Schema(description = "Supported grant types", example = "[\"authorization_code\", \"refresh_token\"]")
    private Set<String> authorizationGrantTypes;

    @Schema(description = "Client authentication methods", example = "[\"client_secret_basic\"]")
    private Set<String> clientAuthenticationMethods;

    @Schema(description = "Redirect URIs for authorization code flow", example = "[\"http://localhost:3000/callback\"]")
    private Set<String> redirectUris;

    @Schema(description = "Post-logout redirect URIs", example = "[\"http://localhost:3000\"]")
    private Set<String> postLogoutRedirectUris;

    @NotEmpty(message = "Scopes are required")
    @Schema(description = "Requested OAuth2 scopes", example = "[\"read:orders\", \"write:orders\"]")
    private Set<String> scopes;
}

