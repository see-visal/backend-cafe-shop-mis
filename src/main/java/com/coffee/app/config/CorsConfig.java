package com.coffee.app.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
   @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001,http://localhost:5173,http://localhost:5174}")
   private String allowedOrigins;
   @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
   private String allowedMethods;
   @Value("${app.cors.allowed-headers:*}")
   private String allowedHeaders;
   @Value("${app.cors.exposed-headers:Authorization,X-Total-Count,Content-Type}")
   private String exposedHeaders;
   @Value("${app.cors.allow-credentials:true}")
   private boolean allowCredentials;
   @Value("${app.cors.max-age:3600}")
   private long maxAge;

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowedOrigins(Arrays.asList(this.allowedOrigins.split(",")));
      config.setAllowedMethods(Arrays.asList(this.allowedMethods.split(",")));
      config.setAllowedHeaders(this.allowedHeaders.equals("*") ? List.of("*") : Arrays.asList(this.allowedHeaders.split(",")));
      config.setExposedHeaders(Arrays.asList(this.exposedHeaders.split(",")));
      config.setAllowCredentials(this.allowCredentials);
      config.setMaxAge(this.maxAge);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);
      return source;
   }
}
