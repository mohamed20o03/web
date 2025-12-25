package com.abdelwahab.CampusCard.domain.common.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

/**
 * Service responsible for JWT token generation, validation, and claim extraction.
 * Uses JJWT library with HS256 algorithm for token signing.
 *
 * <p>Token structure:
 * <ul>
 *   <li><strong>Subject:</strong> User email address</li>
 *   <li><strong>Claims:</strong> userId (Long), role (String)</li>
 *   <li><strong>Issued At:</strong> Current timestamp</li>
 *   <li><strong>Expiration:</strong> 24 hours from issue (configurable)</li>
 * </ul>
 *
 * <p>Configuration:
 * <ul>
 *   <li><strong>jwt.secret:</strong> Plain text secret key (minimum 32 characters for HS256)</li>
 *   <li><strong>jwt.expiration:</strong> Token lifespan in milliseconds (default: 24 hours)</li>
 * </ul>
 *
 * <p>Security considerations:
 * <ul>
 *   <li>Secret key must be stored in environment variables in production</li>
 *   <li>Tokens are stateless and cannot be revoked before expiration</li>
 *   <li>Use HTTPS to prevent token interception</li>
 * </ul>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Generates a JWT token for authenticated user.
     * Token contains user email as subject and userId, role as claims.
     *
     * @param email the user's email address (used as token subject)
     * @param userId the user's unique identifier
     * @param role the user's role (STUDENT or ADMIN)
     * @return signed JWT token string valid for 24 hours
     */
    public String generateToken(String email, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (email) from the JWT token.
     *
     * @param token the JWT token
     * @return the username (email) stored in token subject
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the user ID from the JWT token claims.
     *
     * @param token the JWT token
     * @return the user ID stored in token claims
     */
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    /**
     * Extracts the user role from the JWT token claims.
     *
     * @param token the JWT token
     * @return the user role (STUDENT or ADMIN) stored in token claims
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates if a JWT token is valid for the given username.
     * Checks both username match and token expiration.
     *
     * @param token the JWT token to validate
     * @param username the expected username (email) to match against token subject
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
