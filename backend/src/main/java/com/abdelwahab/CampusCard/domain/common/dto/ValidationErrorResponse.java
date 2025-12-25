package com.abdelwahab.CampusCard.domain.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Specialized error response for validation failures.
 * 
 * <p>This DTO extends the standard error response with field-level validation errors,
 * making it easy for clients to display specific error messages next to form fields.
 * 
 * <p>Example JSON response:
 * <pre>
 * {
 *   "timestamp": "2025-12-24T18:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation failed",
 *   "path": "/api/signup",
 *   "errors": {
 *     "email": "Invalid email format",
 *     "password": "Password must be at least 8 characters",
 *     "phone": "Phone number must be between 10 and 20 digits"
 *   }
 * }
 * </pre>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public record ValidationErrorResponse(
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> errors
) {
    /**
     * Creates a new ValidationErrorResponse with current timestamp.
     * 
     * @param status HTTP status code (typically 400)
     * @param error HTTP status reason phrase
     * @param message overall validation error message
     * @param path the request path where validation failed
     * @param errors map of field names to error messages
     * @return new ValidationErrorResponse instance
     */
    public static ValidationErrorResponse of(int status, String error, String message, String path, Map<String, String> errors) {
        return new ValidationErrorResponse(LocalDateTime.now(), status, error, message, path, errors);
    }
}
