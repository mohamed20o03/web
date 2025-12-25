package com.abdelwahab.CampusCard.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests.
 * Used for authentication via email or national ID.
 *
 * <p>Identifier can be either:
 * <ul>
 *   <li><strong>Email:</strong> User's registered PSU email (e.g., student@eng.psu.edu.eg)</li>
 *   <li><strong>National ID:</strong> 14-digit Egyptian national ID number</li>
 * </ul>
 *
 * <p>Validation:
 * <ul>
 *   <li>Identifier: Required, not blank</li>
 *   <li>Password: Required, not blank</li>
 * </ul>
 *
 * @param identifier user's email address or national ID
 * @param password user's password (plain text, will be compared with hashed password)
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Schema(description = "Login request credentials with email/nationalId and password")
public record  LoginRequest (

    @Schema(
        description = "User identifier - either email address or 14-digit national ID",
        example = "student@eng.psu.edu.eg",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Identifier is required")
    String identifier,

    @Schema(
        description = "User password (minimum 8 characters)",
        example = "SecurePass123",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 8
    )
    @NotBlank(message = "password is required")
    String password
) {};
