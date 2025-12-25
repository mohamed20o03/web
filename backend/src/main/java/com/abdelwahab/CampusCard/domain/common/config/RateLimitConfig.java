package com.abdelwahab.CampusCard.domain.common.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.abdelwahab.CampusCard.domain.common.security.RateLimitInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

/**
 * Configuration for rate limiting on authentication endpoints.
 * Implements rate limiting to prevent brute force attacks on login and signup endpoints.
 * 
 * <p>Rate limits are configured via environment variables:
 * <ul>
 *   <li>Login: RATE_LIMIT_LOGIN_MAX_ATTEMPTS attempts per RATE_LIMIT_LOGIN_WINDOW_MINUTES minutes</li>
 *   <li>Signup: RATE_LIMIT_SIGNUP_MAX_ATTEMPTS attempts per RATE_LIMIT_SIGNUP_WINDOW_MINUTES minutes</li>
 * </ul>
 * 
 * <p>Uses Bucket4j token bucket algorithm for efficient rate limiting.
 * Rate limits are applied per IP address.
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {
    
    @Value("${app.ratelimit.login.maxAttempts}")
    private int loginMaxAttempts;
    
    @Value("${app.ratelimit.login.windowMinutes}")
    private int loginWindowMinutes;
    
    @Value("${app.ratelimit.signup.maxAttempts}")
    private int signupMaxAttempts;
    
    @Value("${app.ratelimit.signup.windowMinutes}")
    private int signupWindowMinutes;
    
    /**
     * Registers the rate limiting interceptor for authentication endpoints.
     * 
     * @param registry InterceptorRegistry to register custom interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Create rate limit bucket for login endpoint
        Bucket loginBucket = createBucket(loginMaxAttempts, loginWindowMinutes);
        
        // Create rate limit bucket for signup endpoint
        Bucket signupBucket = createBucket(signupMaxAttempts, signupWindowMinutes);
        
        // Register rate limit interceptors for specific endpoints
        registry.addInterceptor(new RateLimitInterceptor(loginBucket, "login"))
                .addPathPatterns("/api/login");
        
        registry.addInterceptor(new RateLimitInterceptor(signupBucket, "signup"))
                .addPathPatterns("/api/signup");
    }
    
    /**
     * Creates a Bucket4j bucket with specified capacity and refill rate.
     * 
     * @param maxAttempts maximum number of requests allowed in the time window
     * @param windowMinutes duration of the time window in minutes
     * @return Bucket configured with token bucket rate limiting
     */
    private Bucket createBucket(int maxAttempts, int windowMinutes) {
        // Define bandwidth: maxAttempts tokens, refilled at rate of maxAttempts per windowMinutes
        Bandwidth limit = Bandwidth.classic(
            maxAttempts,
            Refill.intervally(maxAttempts, Duration.ofMinutes(windowMinutes))
        );
        
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
