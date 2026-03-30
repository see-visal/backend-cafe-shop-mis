package com.coffee.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
   info = @Info(
   title = "Coffee Shop Backend API",
   description = "RESTful API for the Coffee Shop e-commerce platform",
   version = "1.0.0",
   contact = @Contact(
   name = "Coffee Shop",
   url = "https://coffeeshop.local"
),
   license = @License(
   name = "MIT"
)
),
   servers = {
           @Server(
   url = "http://localhost:8080",
   description = "Local Development"
), @Server(
   url = "http://localhost:3000",
   description = "Frontend"
)},
   security = {@SecurityRequirement(
   name = "bearerAuth"
)}
)
@SecurityScheme(
   name = "bearerAuth",
   type = SecuritySchemeType.HTTP,
   scheme = "bearer",
   bearerFormat = "JWT",
   description = "JWT Bearer token authentication",
   in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
