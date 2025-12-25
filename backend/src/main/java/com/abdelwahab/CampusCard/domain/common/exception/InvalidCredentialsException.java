package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when user authentication fails due to invalid credentials.
 * 
 * <p>This exception is thrown during login when:
 * <ul>
 *   <li>Email/National ID does not exist</li>
 *   <li>Password is incorrect</li>
 *   <li>Account is locked or disabled</li>
 * </ul>
 * 
 * <p>HTTP Status: 401 UNAUTHORIZED
 * 
 * <p>Security note: The error message should not reveal whether the identifier
 * or password was incorrect to prevent user enumeration attacks.
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class InvalidCredentialsException extends RuntimeException {
    
    /**
     * Constructs a new InvalidCredentialsException with the specified message.
     * 
     * @param message the error message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidCredentialsException with default message.
     */
    public InvalidCredentialsException() {
        super("Invalid email/national ID or password");
    }
}
