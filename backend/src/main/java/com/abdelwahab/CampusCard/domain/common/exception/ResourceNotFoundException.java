package com.abdelwahab.CampusCard.domain.common.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * 
 * <p>This exception is used across all domains when entities cannot be located
 * by their identifiers (e.g., user not found, profile not found, faculty not found).
 * 
 * <p>HTTP Status: 404 NOT FOUND
 * 
 * <p>Usage examples:
 * <pre>
 * throw new ResourceNotFoundException("User", "id", userId);
 * throw new ResourceNotFoundException("Faculty", "id", facultyId);
 * throw new ResourceNotFoundException("Profile", "userId", userId);
 * </pre>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    /**
     * Constructs a new ResourceNotFoundException with resource details.
     * 
     * @param resourceName the name of the resource (e.g., "User", "Profile")
     * @param fieldName the field used for lookup (e.g., "id", "email")
     * @param fieldValue the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Simple constructor with just a message.
     * 
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
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
