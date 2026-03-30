package com.coffee.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2TokenResponse {

    @JsonProperty("access_token")
    @Schema(description = "JWT access token for API requests")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "Refresh token for obtaining new access tokens")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "Token type (always Bearer)", example = "Bearer")
    private String tokenType;

    @JsonProperty("expires_in")
    @Schema(description = "Token expiration time in seconds", example = "900")
    private long expiresIn;

    @JsonProperty("scope")
    @Schema(description = "Granted scopes")
    private String scope;
}

