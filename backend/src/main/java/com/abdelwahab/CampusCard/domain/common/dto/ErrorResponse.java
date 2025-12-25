package com.abdelwahab.CampusCard.domain.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Standardized error response DTO returned to clients when exceptions occur.
 * 
 * <p>This DTO provides consistent error information across all API endpoints:
 * <ul>
 *   <li>Timestamp of when the error occurred</li>
 *   <li>HTTP status code</li>
 *   <li>User-friendly error message</li>
 *   <li>Request path where error occurred</li>
 * </ul>
 * 
 * <p>Example JSON response:
 * <pre>
 * {
 *   "timestamp": "2025-12-24T18:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "User not found with id: '123'",
 *   "path": "/api/users/123"
 * }
 * </pre>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public record ErrorResponse(
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
    /**
     * Creates a new ErrorResponse with current timestamp.
     * 
     * @param status HTTP status code
     * @param error HTTP status reason phrase (e.g., "Not Found", "Bad Request")
     * @param message detailed error message
     * @param path the request path where error occurred
     * @return new ErrorResponse instance
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path);
    }
}
