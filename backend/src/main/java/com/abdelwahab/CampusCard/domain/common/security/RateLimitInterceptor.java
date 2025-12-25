package com.abdelwahab.CampusCard.domain.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

/**
 * Interceptor for rate limiting HTTP requests.
 * Uses Bucket4j token bucket algorithm to limit the number of requests per time window.
 * 
 * <p>When rate limit is exceeded, returns HTTP 429 (Too Many Requests) status.
 * Rate limits are applied per IP address to prevent brute force attacks.
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Bucket bucket;
    private final String endpointName;
    
    /**
     * Creates a new rate limit interceptor for a specific endpoint.
     * 
     * @param bucket Bucket4j bucket configured with rate limit parameters
     * @param endpointName name of the endpoint for logging purposes
     */
    public RateLimitInterceptor(Bucket bucket, String endpointName) {
        this.bucket = bucket;
        this.endpointName = endpointName;
    }
    
    /**
     * Intercepts requests before they reach the controller.
     * Checks if the request is within rate limits.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute
     * @return true if the request should proceed, false if rate limit exceeded
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        // Try to consume 1 token from the bucket
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Request allowed - add remaining tokens to response header
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            // Rate limit exceeded - reject request
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\": \"Rate limit exceeded for %s endpoint\", " +
                "\"message\": \"Too many requests. Please try again in %d seconds.\", " +
                "\"retryAfter\": %d}",
                endpointName, waitForRefill, waitForRefill
            ));
            
            return false;
        }
    }
}
