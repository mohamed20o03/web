package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * 
 * <p>This exception is used when unique constraints are violated, such as:
 * <ul>
 *   <li>Email address already registered</li>
 *   <li>National ID already in use</li>
 *   <li>Banned word already exists</li>
 * </ul>
 * 
 * <p>HTTP Status: 409 CONFLICT
 * 
 * <p>Usage examples:
 * <pre>
 * throw new DuplicateResourceException("User", "email", email);
 * throw new DuplicateResourceException("User", "nationalId", nationalId);
 * throw new DuplicateResourceException("BannedWord", "word", word);
 * </pre>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class DuplicateResourceException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    /**
     * Constructs a new DuplicateResourceException with resource details.
     * 
     * @param resourceName the name of the resource (e.g., "User", "BannedWord")
     * @param fieldName the field that has the duplicate value (e.g., "email", "nationalId")
     * @param fieldValue the duplicate value
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Simple constructor with just a message.
     * 
     * @param message the error message
     */
    public DuplicateResourceException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.fieldName = "unknown";
        this.fieldValue = null;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
