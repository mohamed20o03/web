package com.abdelwahab.CampusCard.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abdelwahab.CampusCard.domain.auth.dto.SignUpRequest;
import com.abdelwahab.CampusCard.domain.auth.dto.SignUpResponse;
import com.abdelwahab.CampusCard.domain.auth.service.SignUpService;

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

/**
 * REST Controller for user registration endpoints.
 * Handles new student account creation with multipart form data.
 *
 * <p>Endpoint: {@code POST /api/signup}
 *
 * <p>Rate limiting: This endpoint is protected by rate limiting configuration
 * to prevent abuse (3 attempts per hour per IP).
 *
 * <p>Registration requirements:
 * <ul>
 *   <li>Valid PSU email address (@eng.psu.edu.eg)</li>
 *   <li>14-digit Egyptian national ID</li>
 *   <li>Strong password (min 8 characters)</li>
 *   <li>Valid faculty and department IDs</li>
 *   <li>Academic year within faculty range</li>
 *   <li>National ID scan photo (JPEG/PNG)</li>
 * </ul>
 *
 * <p>New users are created with PENDING status and require:
 * <ol>
 *   <li>Email verification by clicking link sent to registered email</li>
 *   <li>Admin approval after manual verification of profile photo vs national ID scan</li>
 * </ol>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and account creation endpoints")
public class SignUpController {
    
    private final SignUpService signUpService;

    /**
     * Registers a new student account with complete validation.
     * Accepts multipart form data including profile photo and national ID scan.
     *
     * <p>Validation performed:
     * <ul>
     *   <li>Email uniqueness and PSU domain validation</li>
     *   <li>National ID format and uniqueness</li>
     *   <li>Faculty and department existence and relationship</li>
     *   <li>Academic year validity for selected faculty</li>
     *   <li>File upload validation (size, format)</li>
     * </ul>
     *
     * <p>HTTP Status Codes:
     * <ul>
     *   <li>201 CREATED - Registration successful, user created with PENDING status</li>
     *   <li>400 BAD_REQUEST - Validation failed or duplicate email/national ID</li>
     *   <li>429 TOO_MANY_REQUESTS - Rate limit exceeded</li>
     * </ul>
     *
     * @param request signup form data with user details and uploaded files
     * @return SignUpResponse with success message and user email
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Register new student account",
        description = """
            Creates a new student account with PENDING status. User must verify email
            and wait for admin approval before being able to login.
            
            **Registration Process:**
            1. Submit signup form with profile photo and national ID scan
            2. System validates all fields and creates user with PENDING status
            3. Verification email sent to provided address
            4. User clicks verification link to confirm email
            5. Admin reviews profile and approves/rejects
            6. Upon approval, user can login
            
            **Requirements:**
            - PSU email address (@eng.psu.edu.eg)
            - 14-digit Egyptian National ID
            - Strong password (min 8 chars)
            - Valid faculty and department
            - Profile photo (JPEG/PNG, max 10MB)
            - National ID scan (JPEG/PNG, max 10MB)
            
            **Rate Limiting:**
            Limited to 3 registration attempts per hour per IP.
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Registration successful - User created with PENDING status",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignUpResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": "SUCCESS",
                          "message": "Registration successful! Please check your email to verify your account.",
                          "email": "student@eng.psu.edu.eg"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed or duplicate email/national ID",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignUpResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "status": "ERROR",
                          "message": "Email already registered",
                          "email": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Too many registration attempts - Rate limit exceeded"
        )
    })
    public ResponseEntity<SignUpResponse> createNewUser(
        @Parameter(
            description = "Multipart form data with user registration details and uploaded files",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        @Valid @ModelAttribute SignUpRequest request
    ) {
        try {
            SignUpResponse signUpResponse = signUpService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse); // 201
        } catch (RuntimeException e) {
            SignUpResponse errorResponse = SignUpResponse.builder()
                .status("ERROR")
                .message(e.getMessage())
                .build();
            return ResponseEntity.badRequest().body(errorResponse); // 400
        }
    }
    
}

