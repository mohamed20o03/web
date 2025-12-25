package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when a verification or authentication token is invalid or expired.
 * 
 * <p>This exception is used for token-related errors:
 * <ul>
 *   <li>Email verification token is invalid</li>
 *   <li>Verification token has expired</li>
 *   <li>JWT token is malformed or expired</li>
 * </ul>
 * 
 * <p>HTTP Status: 400 BAD REQUEST
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class InvalidTokenException extends RuntimeException {
    
    /**
     * Constructs a new InvalidTokenException with the specified message.
     * 
     * @param message the error message
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
