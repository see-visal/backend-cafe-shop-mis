package com.coffee.app.service;

import com.coffee.app.domain.Role;
import com.coffee.app.domain.User;
import com.coffee.app.dto.request.LoginRequest;
import com.coffee.app.dto.request.RefreshTokenRequest;
import com.coffee.app.dto.request.RegisterRequest;
import com.coffee.app.dto.response.AuthResponse;
import com.coffee.app.repository.RoleRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.security.CustomUserDetails;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
   private static final String DEFAULT_CLIENT_ID = "salsee-web-app";
   
   private final DaoAuthenticationProvider authenticationProvider;
   private final UserDetailsService userDetailsService;
   private final UserRepository userRepository;
   private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;
   private final OAuth2TokenService oauth2TokenService;
   private final JwtDecoder oAuth2JwtDecoder;

   public AuthService(DaoAuthenticationProvider authenticationProvider, UserDetailsService userDetailsService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, OAuth2TokenService oauth2TokenService, JwtDecoder oAuth2JwtDecoder) {
      this.authenticationProvider = authenticationProvider;
      this.userDetailsService = userDetailsService;
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
      this.passwordEncoder = passwordEncoder;
      this.oauth2TokenService = oauth2TokenService;
      this.oAuth2JwtDecoder = oAuth2JwtDecoder;
   }

   public AuthResponse login(LoginRequest request) {
      Authentication auth = this.authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
      CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
      return this.buildTokenPair(user);
   }

   @Transactional
   public AuthResponse register(RegisterRequest request) {
      if (this.userRepository.existsByUsername(request.username())) {
         throw new IllegalArgumentException("Username already taken");
      } else if (this.userRepository.existsByEmail(request.email())) {
         throw new IllegalArgumentException("Email already registered");
      } else {
         User user = new User();
         user.setUuid(UUID.randomUUID().toString());
         user.setUsername(request.username());
         user.setEmail(request.email());
         user.setPassword(this.passwordEncoder.encode(request.password()));
         user.setGivenName(request.givenName());
         user.setFamilyName(request.familyName());
         user.setPhoneNumber(request.phoneNumber());
         user.setGender("N/A");
         user.setProfileImage("");
         user.setCoverImage("");
         user.setAccountNonExpired(true);
         user.setAccountNonLocked(true);
         user.setCredentialsNonExpired(true);
         user.setIsEnabled(true);
         Set<Role> roles = new HashSet<>();
         Role customerRole = this.roleRepository.findByName("CUSTOMER").orElseThrow(() -> new IllegalStateException("Default role not found: CUSTOMER"));
         roles.add(customerRole);
         user.setRoles(roles);
         user.setPermissions(new HashSet<>());
         this.userRepository.save(user);
         CustomUserDetails details = (CustomUserDetails)this.userDetailsService.loadUserByUsername(user.getUsername());
         return this.buildTokenPair(details);
      }
   }

   public AuthResponse refresh(RefreshTokenRequest request) {
      log.debug("[AuthService] Refresh token request");

      if (oauth2TokenService.isTokenBlacklisted(request.refreshToken())) {
         throw new BadCredentialsException("Refresh token has been revoked");
      }

      final Jwt refreshJwt;
      try {
         refreshJwt = oAuth2JwtDecoder.decode(request.refreshToken());
      } catch (JwtException ex) {
         throw new BadCredentialsException("Invalid refresh token", ex);
      }

      String tokenType = refreshJwt.getClaimAsString("token_type");
      if (!"Refresh".equalsIgnoreCase(tokenType)) {
         throw new BadCredentialsException("Token is not a refresh token");
      }

      String username = refreshJwt.getSubject();
      if (username == null || username.isBlank()) {
         throw new BadCredentialsException("Refresh token subject is missing");
      }

      CustomUserDetails user = (CustomUserDetails) this.userDetailsService.loadUserByUsername(username);
      return this.buildTokenPair(user);
   }

   private AuthResponse buildTokenPair(CustomUserDetails user) {
      log.debug("[AuthService] Building token pair for user: {}", user.getUsername());
      
      // Generate default scopes based on user roles
      Set<String> scopes = user.getRoles().stream()
              .map(role -> "role:" + role.toLowerCase())
              .collect(Collectors.toSet());
      scopes.add("read:profile");
      scopes.add("write:profile");
      
      // Use OAuth2TokenService to generate tokens
      OAuth2TokenService.OAuth2TokenResponse oauthResponse = oauth2TokenService.generateTokenPair(user, DEFAULT_CLIENT_ID, scopes);
      
      // Map to AuthResponse format for backward compatibility
      AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(user.getUuid(), user.getUsername(), user.getEmail(), user.getGivenName(), user.getFamilyName(), user.getRoles());
      return AuthResponse.of(oauthResponse.getAccessToken(), oauthResponse.getRefreshToken(), oauthResponse.getExpiresIn(), userInfo);
   }
}
