package com.abdelwahab.CampusCard.domain.common.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        // Get Authorization header from request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // If no Authorization header or doesn't start with "Bearer ", skip JWT validation
        // Note: "Bearer " is case-sensitive and must have a space
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token from "Bearer <token>"
        jwt = authHeader.substring(7);
        
        // Extract email (username) from JWT token
        email = jwtService.extractUsername(jwt);

        // If email exists and user is not already authenticated
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load user from database
            User user = userRepository.findByEmail(email);
            
            // If user exists and token is valid
            if(user != null && jwtService.isTokenValid(jwt, user.getEmail())) {

                // Create authentication token with user details and authorities (roles)
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
