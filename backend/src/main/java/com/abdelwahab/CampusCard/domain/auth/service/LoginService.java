package com.abdelwahab.CampusCard.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abdelwahab.CampusCard.domain.auth.dto.LoginRequest;
import com.abdelwahab.CampusCard.domain.auth.dto.LoginResponse;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.common.security.JwtService;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidCredentialsException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for user authentication and login operations.
 * Supports login via email or national ID with JWT token generation.
 *
 * <p>This service handles:
 * <ul>
 *   <li>Email-based authentication</li>
 *   <li>National ID-based authentication</li>
 *   <li>JWT token generation and validation</li>
 *   <li>Password verification using BCrypt</li>
 * </ul>
 *
 * <p>Users with PENDING or REJECTED status can log in, but their access
 * is restricted at the feature level. Only APPROVED users have full access.
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LoginService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticates a user and generates a JWT token.
     * Accepts either email address or national ID as identifier.
     *
     * <p>The method:
     * <ol>
     *   <li>Determines if identifier is email or national ID</li>
     *   <li>Retrieves user from database</li>
     *   <li>Verifies password using BCrypt</li>
     *   <li>Generates JWT token with user details</li>
     *   <li>Returns login response with token and user info</li>
     * </ol>
     *
     * @param request the login credentials containing identifier (email or national ID) and password
     * @return LoginResponse containing JWT token, user ID, email, role, status, and success message
     * @throws InvalidCredentialsException if user not found or password is incorrect
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        String identifier = request.identifier();
        User user;

        // Check if identifier is email format, otherwise treat as national ID
        if(identifier.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            // Use instance method (userRepository) not class (UserRepository)
            user = userRepository.findByEmail(request.identifier());
            if (user == null) {
                throw new InvalidCredentialsException();
            }
        } else {
            // Use instance method (userRepository) not class (UserRepository)
            user = userRepository.findByNationalId(request.identifier());
            if (user == null) {
                throw new InvalidCredentialsException();
            }
        }

        // Verify password matches
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Note: PENDING and REJECTED users can login, but their accounts will be private
        // Access control will be handled at the feature level based on status

        // Generate JWT token
        String token = jwtService.generateToken(
            user.getEmail(),
            user.getId().longValue(),
            user.getRole().name()
        );

        // Return response with correct parameter order: token, id, email, role, status, message
        return new LoginResponse(
            token,
            user.getId().longValue(), 
            user.getEmail(),
            user.getRole().getValue(),
            user.getStatus().name(),
            "Login successful"
        );
    }
}
