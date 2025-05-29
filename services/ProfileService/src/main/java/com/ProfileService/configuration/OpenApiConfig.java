package com.ProfileService.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(title = "API document", version = "1.0.0", description = "Description of API document"),
        servers = {@Server(url = "http://localhost:8085", description = "Local Development Server")},
        security = {@SecurityRequirement(name = "bearerAuth")})
@SecuritySchemes({
    @SecurityScheme(
            name = "bearerAuth",
            type = SecuritySchemeType.HTTP,
            scheme = "bearer",
            bearerFormat = "JWT",
            description = "JWT Authorization header using the Bearer scheme")
})
@Configuration
public class OpenApiConfig {}
