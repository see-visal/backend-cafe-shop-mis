package com.coffee.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientResponse {

    @JsonProperty("client_id")
    @Schema(description = "Unique OAuth2 client identifier")
    private String clientId;

    @JsonProperty("client_secret")
    @Schema(description = "Client secret (keep secure and never expose to frontend)")
    private String clientSecret;

    @JsonProperty("client_name")
    @Schema(description = "Human-readable client name")
    private String clientName;

    @JsonProperty("created_at")
    @Schema(description = "Client creation timestamp")
    private String createdAt;
}

