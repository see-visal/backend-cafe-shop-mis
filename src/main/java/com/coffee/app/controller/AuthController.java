package com.coffee.app.controller;

import com.coffee.app.dto.request.LoginRequest;
import com.coffee.app.dto.request.RefreshTokenRequest;
import com.coffee.app.dto.request.RegisterRequest;
import com.coffee.app.dto.response.AuthResponse;
import com.coffee.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/public/auth"})
@Tag(name = "Authentication", description = "User authentication and token management endpoints")
public class AuthController {
   private final AuthService authService;

   @Operation(
      summary = "User login",
      description = "Authenticate user with credentials and return OAuth2 access + refresh tokens"
   )
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful, tokens returned"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials")
   })
   @PostMapping({"/login"})
   public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
      return ResponseEntity.ok(this.authService.login(request));
   }

   @Operation(
      summary = "User registration",
      description = "Register new customer account and return OAuth2 tokens"
   )
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Registration successful"),
      @ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
      @ApiResponse(responseCode = "409", description = "Username or email already taken")
   })
   @PostMapping({"/register"})
   public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
      return ResponseEntity.status(201).body(this.authService.register(request));
   }

   @Operation(
      summary = "Refresh access token",
      description = "Exchange refresh token for new access token"
   )
   @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New token returned"),
      @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
      @ApiResponse(responseCode = "401", description = "Token expired or invalid")
   })
   @PostMapping({"/refresh"})
   public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
      return ResponseEntity.ok(this.authService.refresh(request));
   }

   @Generated
   public AuthController(final AuthService authService) {
      this.authService = authService;
   }
}
