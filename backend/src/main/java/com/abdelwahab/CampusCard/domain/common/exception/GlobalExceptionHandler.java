package com.abdelwahab.CampusCard.domain.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.abdelwahab.CampusCard.domain.common.dto.ErrorResponse;
import com.abdelwahab.CampusCard.domain.common.dto.ValidationErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the CampusCard application.
 * 
 * <p>This class uses @RestControllerAdvice to handle exceptions across all controllers
 * in a centralized manner, providing consistent error responses to clients.
 * 
 * <p>Handled exception types:
 * <ul>
 *   <li>{@link ResourceNotFoundException} - 404 NOT FOUND</li>
 *   <li>{@link InvalidCredentialsException} - 401 UNAUTHORIZED</li>
 *   <li>{@link UnauthorizedException} - 403 FORBIDDEN</li>
 *   <li>{@link DuplicateResourceException} - 409 CONFLICT</li>
 *   <li>{@link InvalidStateException} - 400 BAD REQUEST</li>
 *   <li>{@link InvalidTokenException} - 400 BAD REQUEST</li>
 *   <li>{@link MethodArgumentNotValidException} - 400 BAD REQUEST (validation)</li>
 *   <li>{@link RuntimeException} - 500 INTERNAL SERVER ERROR</li>
 * </ul>
 * 
 * <p>All exceptions are logged for debugging and monitoring purposes.
 * 
 * @author CampusCard Team
 * @version 2.0
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException (404 NOT FOUND).
     * 
     * <p>Thrown when requested entity does not exist (user, profile, faculty, etc.).
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles InvalidCredentialsException (401 UNAUTHORIZED).
     * 
     * <p>Thrown when login credentials are invalid or account is locked.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 401 status
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Handles UnauthorizedException (403 FORBIDDEN).
     * 
     * <p>Thrown when user lacks permission to perform an action.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 403 status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex, WebRequest request) {
        log.warn("Authorization failed: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handles DuplicateResourceException (409 CONFLICT).
     * 
     * <p>Thrown when attempting to create a resource that already exists.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 409 status
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handles InvalidStateException (400 BAD REQUEST).
     * 
     * <p>Thrown when operation cannot proceed due to invalid resource state.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 400 status
     */
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(
            InvalidStateException ex, WebRequest request) {
        log.warn("Invalid state: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles InvalidTokenException (400 BAD REQUEST).
     * 
     * <p>Thrown when token is invalid or expired.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 400 status
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, WebRequest request) {
        log.warn("Invalid token: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles validation errors when @Valid annotation fails on request bodies.
     * 
     * <p>When a client sends invalid data (e.g., invalid phone number, email format, etc.),
     * Spring's Bean Validation throws MethodArgumentNotValidException. This method
     * catches that exception and returns field-level error details.
     * 
     * <p>Example scenarios:
     * <ul>
     *   <li>Invalid phone number format: "phone: Phone number must be between 10 and 20 digits"</li>
     *   <li>Invalid LinkedIn URL: "linkedin: Invalid LinkedIn URL"</li>
     *   <li>Invalid visibility value: "visibility: Visibility must be PUBLIC or PRIVATE"</li>
     * </ul>
     * 
     * @param ex the exception containing all validation errors from the request
     * @param request the web request
     * @return ValidationErrorResponse with 400 status and field-level errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        
        // Extract field errors into a map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ValidationErrorResponse error = ValidationErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Validation failed",
            getRequestPath(request),
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles all other RuntimeExceptions (500 INTERNAL SERVER ERROR).
     * 
     * <p>This is a catch-all handler for unexpected errors. The full exception
     * is logged for debugging while a generic message is returned to the client
     * to avoid exposing internal implementation details.
     * 
     * @param ex the exception
     * @param request the web request
     * @return standardized error response with 500 status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            getRequestPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Extracts the request path from WebRequest for error response.
     * 
     * @param request the web request
     * @return the request path (e.g., "/api/users/123")
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
