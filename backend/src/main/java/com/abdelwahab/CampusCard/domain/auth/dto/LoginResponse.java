package com.abdelwahab.CampusCard.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for login response.
 * Contains JWT token and user information upon successful authentication.
 * 
 * @param token JWT authentication token (null on error)
 * @param id User's database ID
 * @param email User's email address
 * @param role User's role (student, admin)
 * @param status Response status (SUCCESS or ERROR)
 * @param message Success or error message
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Schema(description = "Login response with JWT token and user information")
public record LoginResponse(
    
    @Schema(
        description = "JWT authentication token (valid for 24 hours)",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdHVkZW50QGVuZy5wc3UuZWR1LmVnIiwicm9sZSI6InN0dWRlbnQiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDA4NjQwMH0.signature",
        nullable = true
    )
    String token,
    
    @Schema(
        description = "User's unique database identifier",
        example = "1",
        nullable = true
    )
    Long id,
    
    @Schema(
        description = "User's email address",
        example = "student@eng.psu.edu.eg",
        nullable = true
    )
    String email,
    
    @Schema(
        description = "User's role in the system",
        example = "student",
        allowableValues = {"student", "admin"},
        nullable = true
    )
    String role,
    
    @Schema(
        description = "Response status indicator",
        example = "SUCCESS",
        allowableValues = {"SUCCESS", "ERROR"}
    )
    String status,
    
    @Schema(
        description = "Human-readable response message",
        example = "Login successful"
    )
    String message
) {}

