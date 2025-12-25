package com.abdelwahab.CampusCard.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abdelwahab.CampusCard.domain.auth.dto.LoginRequest;
import com.abdelwahab.CampusCard.domain.auth.dto.LoginResponse;
import com.abdelwahab.CampusCard.domain.auth.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for user authentication endpoints.
 * Handles login operations with email or national ID.
 *
 * <p>Endpoint: {@code POST /api/login}
 *
 * <p>Rate limiting: This endpoint is protected by rate limiting configuration
 * to prevent brute force attacks (5 attempts per 15 minutes per IP).
 *
 * <p>Authentication flow:
 * <ol>
 *   <li>Client sends credentials (email/nationalId + password)</li>
 *   <li>Controller validates request format</li>
 *   <li>Service authenticates user</li>
 *   <li>JWT token generated on success</li>
 *   <li>Token returned to client for subsequent requests</li>
 * </ol>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/login")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints for login operations")
public class LoginController {
    
    private final LoginService loginService;

    /**
     * Authenticates user with email or national ID and returns JWT token.
     *
     * <p>Accepts either email address or 14-digit national ID as identifier.
     * Returns JWT token valid for 24 hours on successful authentication.
     *
     * <p>HTTP Status Codes:
     * <ul>
     *   <li>200 OK - Authentication successful, token in response</li>
     *   <li>401 UNAUTHORIZED - Invalid credentials</li>
     *   <li>429 TOO_MANY_REQUESTS - Rate limit exceeded</li>
     * </ul>
     *
     * @param request login credentials containing identifier and password
     * @return LoginResponse with JWT token and user details on success, error message on failure
     */
    @PostMapping
    @Operation(
        summary = "Authenticate user and obtain JWT token",
        description = """
            Authenticates a user using email or national ID and password.
            Returns a JWT token that must be included in subsequent authenticated requests.
            
            **Supported Identifiers:**
            - Email address (e.g., student@eng.psu.edu.eg)
            - 14-digit National ID (e.g., 12345678901234)
            
            **Token Usage:**
            Include the returned token in Authorization header:
            `Authorization: Bearer <token>`
            
            **Rate Limiting:**
            Limited to 5 attempts per 15 minutes per IP address.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful - JWT token returned",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                        {
                          "status": "SUCCESS",
                          "message": "Login successful",
                          "email": "student@eng.psu.edu.eg",
                          "role": "student",
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "userStatus": "APPROVED"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication failed - Invalid credentials or user not approved",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                        {
                          "status": "ERROR",
                          "message": "Invalid email or password",
                          "email": null,
                          "role": null,
                          "token": null,
                          "userStatus": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Too many login attempts - Rate limit exceeded",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Too many login attempts. Please try again later."
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<LoginResponse> login(
        @Parameter(
            description = "Login credentials with email/nationalId and password",
            required = true,
            schema = @Schema(implementation = LoginRequest.class)
        )
        @Valid @RequestBody LoginRequest request
    ) {
        log.info("Login attempt for identifier: {}", request.identifier());
        try {
            LoginResponse response = loginService.login(request);
            log.info("Login successful for: {}", request.identifier());
            return ResponseEntity.ok(response); // 200
        } catch (RuntimeException e) {
            log.error("Login failed for {}: {}", request.identifier(), e.getMessage());
            LoginResponse error = new LoginResponse(
                "ERROR",
                null, e.getMessage(),
                null,
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error); // 401
        }
    }
    
}
