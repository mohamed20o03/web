package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when a user attempts an action they are not authorized to perform.
 * 
 * <p>This exception is used for authorization (not authentication) failures:
 * <ul>
 *   <li>Accessing another user's private profile</li>
 *   <li>Modifying resources owned by someone else</li>
 *   <li>Performing admin-only actions without admin role</li>
 *   <li>Changing one's own admin role</li>
 * </ul>
 * 
 * <p>HTTP Status: 403 FORBIDDEN
 * 
 * <p>Note: Use {@link InvalidCredentialsException} for authentication failures (401).
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class UnauthorizedException extends RuntimeException {
    
    /**
     * Constructs a new UnauthorizedException with the specified message.
     * 
     * @param message the error message explaining the authorization failure
     */
    public UnauthorizedException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new UnauthorizedException with default message.
     */
    public UnauthorizedException() {
        super("You do not have permission to perform this action");
    }
}
