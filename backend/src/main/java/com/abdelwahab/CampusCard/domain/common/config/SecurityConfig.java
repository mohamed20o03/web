package com.abdelwahab.CampusCard.domain.common.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.common.security.JwtAuthenticationFilter;
import com.abdelwahab.CampusCard.domain.common.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Security configuration for the CampusCard application.
 * Configures authentication, authorization, CORS, and JWT token-based security.
 * 
 * <p>Key security features:
 * <ul>
 *   <li>JWT token-based authentication (stateless)</li>
 *   <li>BCrypt password encoding</li>
 *   <li>CORS configuration with environment-based origins</li>
 *   <li>Role-based access control (ADMIN role for admin endpoints)</li>
 *   <li>Public access to login, signup, and public profile endpoints</li>
 * </ul>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Configures the password encoder for the application.
     * Uses BCrypt hashing algorithm with default strength (10 rounds).
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and configures the JWT authentication filter.
     * This filter intercepts requests and validates JWT tokens.
     * 
     * @return JwtAuthenticationFilter instance
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * Allowed origins are loaded from environment variables for security.
     * 
     * <p>In development: typically http://localhost:3000
     * <p>In production: should be set to the actual frontend domain(s)
     * 
     * @return CorsConfigurationSource with configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse comma-separated allowed origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        configuration.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedHeader("*"); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (cookies, authorization headers)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Defines authentication and authorization rules for all endpoints.
     * 
     * @param http HttpSecurity object used to configure web-based security
     * @return SecurityFilterChain the built security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS with custom configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF (Cross-Site Request Forgery) protection
            // Safe for stateless REST APIs since we're not using cookies/sessions
            // CSRF protection is mainly needed for browser-based form submissions
            .csrf(csrf -> csrf.disable())
            
            // Disable HTTP Basic authentication prompt
            // We're using custom authentication (JWT tokens), not basic auth
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Disable form login
            // We're using REST API with JSON, not HTML forms
            .formLogin(form -> form.disable())
            
            // Configure session management to be STATELESS
            // This means the server won't create or use HTTP sessions
            // Each request must contain all authentication info (JWT token)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules for HTTP requests
            .authorizeHttpRequests(auth -> auth
                // Allow public access to authentication endpoints
                .requestMatchers("/api/signup", "/api/login").permitAll()
                
                // Allow public access to public API endpoints (faculties, departments)
                .requestMatchers("/api/public/**").permitAll()
                
                // Allow public access to Swagger/OpenAPI documentation
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/swagger-ui.html").permitAll()
                
                // Allow public access to profile endpoints
                // Note: Visibility is enforced at the service layer, not URL level
                .requestMatchers("/api/profile/**").permitAll()
                
                // Admin endpoints - require ADMIN role
                // Only users with ROLE_ADMIN can access /api/admin/**
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        // Register JWT filter before Spring Security's UsernamePasswordAuthenticationFilter
        // This ensures JWT validation happens before other authentication mechanisms
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build and return the configured SecurityFilterChain
        return http.build();
    }
}
