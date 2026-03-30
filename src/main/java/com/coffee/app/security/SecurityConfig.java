package com.coffee.app.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 resource-server configuration.
 * Access/refresh tokens are OAuth2 bearer tokens encoded as RSA-signed JWTs.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
   private final UserDetailsService userDetailsService;
   private final PasswordEncoder passwordEncoder;
   private final JwtKeyStore jwtKeyStore;

   @Primary
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder oAuth2JwtDecoder) throws Exception {
      log.info("[SecurityConfig] Configuring OAuth2 security chain");
      
      return http
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers(HttpMethod.POST, "/api/public/auth/**").permitAll()
                      .requestMatchers("/api/public/**").permitAll()
                      .requestMatchers("/media/**").permitAll()
                      .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                      .requestMatchers("/uploads/**").permitAll()
                      .requestMatchers("/api/customer/**", "/payment/**").hasAnyRole("CUSTOMER", "ADMIN", "BARISTA")
                      .requestMatchers("/api/barista/**").hasAnyRole("BARISTA", "ADMIN")
                      .requestMatchers("/api/admin/**").hasRole("ADMIN")
                      .requestMatchers("/api/oauth2/**").authenticated()
                      .anyRequest().authenticated())
              .oauth2ResourceServer(oauth2 -> oauth2
                      .bearerTokenResolver(publicSkippingBearerTokenResolver())
                      .jwt(jwt -> jwt
                              .decoder(oAuth2JwtDecoder)
                              .jwtAuthenticationConverter(oAuth2JwtAuthenticationConverter())))
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .httpBasic(AbstractHttpConfigurer::disable)
              .formLogin(AbstractHttpConfigurer::disable)
              .cors(Customizer.withDefaults())
              .build();
   }

   @Bean
   public BearerTokenResolver publicSkippingBearerTokenResolver() {
      DefaultBearerTokenResolver delegate = new DefaultBearerTokenResolver();
      return (request) -> {
         String path = request.getRequestURI();
         if (path.startsWith("/api/public/") || path.startsWith("/media/") || path.startsWith("/uploads/") || 
             path.startsWith("/swagger-ui/") || path.equals("/swagger-ui.html") || 
             path.startsWith("/v3/api-docs")) {
            return null;
         }
         return delegate.resolve(request);
      };
   }

   @Bean
   public JwtAuthenticationConverter oAuth2JwtAuthenticationConverter() {
      JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
      converter.setJwtGrantedAuthoritiesConverter((jwt) -> {
         Collection<String> roles = jwt.getClaimAsStringList("roles");
         if (roles == null) {
            return List.of();
         }
         return roles.stream()
                 .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                 .collect(Collectors.toList());
      });
      return converter;
   }

   @Bean
   public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(this.userDetailsService);
      provider.setPasswordEncoder(this.passwordEncoder);
      return provider;
   }

   // ============ OAuth2 JWT Encoder/Decoder Beans ============

   @Primary
   @Bean
   public RSAKey oAuth2RsaKey() {
      return this.jwtKeyStore.loadOrGenerateAccessKey();
   }

   @Primary
   @Bean
   public JWKSource<SecurityContext> oAuth2JwkSource(RSAKey oAuth2RsaKey) {
      JWKSet jwkSet = new JWKSet(oAuth2RsaKey);
      return (selector, ctx) -> selector.select(jwkSet);
   }

   @Primary
   @Bean
   public JwtEncoder oAuth2JwtEncoder(JWKSource<SecurityContext> oAuth2JwkSource) {
      log.info("[SecurityConfig] Creating OAuth2 JwtEncoder");
      return new NimbusJwtEncoder(oAuth2JwkSource);
   }

   @Primary
   @Bean
   public JwtDecoder oAuth2JwtDecoder(RSAKey oAuth2RsaKey) throws JOSEException {
      log.info("[SecurityConfig] Creating OAuth2 JwtDecoder");
      return NimbusJwtDecoder.withPublicKey(oAuth2RsaKey.toRSAPublicKey()).build();
   }

   @Generated
   public SecurityConfig(final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder, final JwtKeyStore jwtKeyStore) {
      this.userDetailsService = userDetailsService;
      this.passwordEncoder = passwordEncoder;
      this.jwtKeyStore = jwtKeyStore;
      log.info("[SecurityConfig] Initialized with OAuth2 resource server configuration");
   }
}

