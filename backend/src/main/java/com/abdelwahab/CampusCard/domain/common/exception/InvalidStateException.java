package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when an operation is attempted on a resource in an invalid state.
 * 
 * <p>This exception is used for business logic violations where the operation
 * cannot proceed due to the current state of the resource:
 * <ul>
 *   <li>Approving a user who is not in PENDING status</li>
 *   <li>Verifying email for an already verified user</li>
 *   <li>Using an expired verification token</li>
 *   <li>Approving a user with unverified email</li>
 * </ul>
 * 
 * <p>HTTP Status: 400 BAD REQUEST
 * 
 * <p>Usage examples:
 * <pre>
 * throw new InvalidStateException("User is not in pending status");
 * throw new InvalidStateException("Email is already verified");
 * throw new InvalidStateException("Verification token has expired");
 * </pre>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class InvalidStateException extends RuntimeException {
    
    /**
     * Constructs a new InvalidStateException with the specified message.
     * 
     * @param message the error message explaining the invalid state
     */
    public InvalidStateException(String message) {
        super(message);
    }
}
