package com.abdelwahab.CampusCard.domain.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for CampusCard API documentation.
 * Provides interactive API documentation at /swagger-ui.html
 * 
 * <p>This configuration defines:
 * <ul>
 *   <li>API metadata (title, version, description)</li>
 *   <li>JWT Bearer authentication scheme</li>
 *   <li>Contact information</li>
 *   <li>Server URLs for different environments</li>
 * </ul>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configures the OpenAPI documentation for CampusCard REST API.
     * 
     * @return OpenAPI configuration with security, metadata, and server information
     */
    @Bean
    public OpenAPI campusCardOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CampusCard API")
                .description("""
                    University Student Directory and Profile Management Platform
                    
                    ## Overview
                    CampusCard is a comprehensive platform for managing student profiles and directories at PSU Engineering.
                    
                    ## Features
                    - User registration and authentication (JWT-based)
                    - Profile management with photo uploads
                    - Admin approval workflow
                    - Content moderation
                    - Student directory with search and filters
                    - Email verification
                    
                    ## Authentication
                    Most endpoints require JWT authentication. Obtain a token by calling POST /api/login,
                    then include it in the Authorization header: `Bearer <token>`
                    
                    ## Rate Limiting
                    Authentication endpoints are rate-limited to prevent abuse:
                    - Login: 5 attempts per 15 minutes
                    - Signup: 3 attempts per hour
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("CampusCard Development Team")
                    .email("Mohamed170408@eng.psu.edu.eg")
                    .url("https://github.com/mohamed20o03/CampusCard"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.campuscard.psu.edu.eg")
                    .description("Production Server (if deployed)")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("""
                        JWT authentication token obtained from POST /api/login endpoint.
                        
                        Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                        
                        The token contains the user's email and role, and expires after 24 hours.
                        """)));
    }
}
