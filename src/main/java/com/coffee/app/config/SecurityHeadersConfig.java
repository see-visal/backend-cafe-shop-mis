package com.coffee.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityHeadersConfig {
   @Bean
   public OncePerRequestFilter securityHeadersFilter() {
      return new OncePerRequestFilter() {
         protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            String path = request.getRequestURI();
            if (!path.startsWith("/swagger-ui") && !path.startsWith("/v3/api-docs")) {
               response.setHeader("X-Frame-Options", "DENY");
               response.setHeader("X-Content-Type-Options", "nosniff");
               response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
               response.setHeader("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'");
            } else {
               response.setHeader("X-Content-Type-Options", "nosniff");
               response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            }

            response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            chain.doFilter(request, response);
         }
      };
   }
}
