package com.abdelwahab.CampusCard.domain.common.constants;

/**
 * Central repository for all user-facing error messages used across the application.
 * Provides consistent, localized error messaging and supports message parameterization.
 *
 * <p>Usage example:
 * <pre>{@code
 * throw new ResourceNotFoundException(
 *     String.format(ErrorMessages.USER_NOT_FOUND, userId)
 * );
 * }</pre>
 *
 * @author CampusCard Team
 * @since 1.0
 */
public final class ErrorMessages {
    
    // Prevent instantiation
    private ErrorMessages() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // =============================================================================
    // AUTHENTICATION ERRORS
    // =============================================================================
    
    /** Error when login credentials are invalid */
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    
    /** Error when user account is not approved yet */
    public static final String ACCOUNT_NOT_APPROVED = "Your account is pending approval";
    
    /** Error when user account has been rejected */
    public static final String ACCOUNT_REJECTED = "Your account has been rejected: %s";
    
    /** Error when email is not verified */
    public static final String EMAIL_NOT_VERIFIED = "Please verify your email address before logging in";
    
    /** Error when JWT token is expired */
    public static final String TOKEN_EXPIRED = "Authentication token has expired";
    
    /** Error when JWT token is invalid */
    public static final String TOKEN_INVALID = "Invalid authentication token";
    
    /** Error when user lacks required permissions */
    public static final String ACCESS_DENIED = "You don't have permission to access this resource";
    
    // =============================================================================
    // USER ERRORS
    // =============================================================================
    
    /** Error when user is not found by identifier */
    public static final String USER_NOT_FOUND = "User not found with identifier: %s";
    
    /** Error when user is not found by ID */
    public static final String USER_NOT_FOUND_BY_ID = "User not found with ID: %d";
    
    /** Error when email is already registered */
    public static final String EMAIL_ALREADY_EXISTS = "Email address is already registered";
    
    /** Error when national ID is already registered */
    public static final String NATIONAL_ID_ALREADY_EXISTS = "National ID is already registered";
    
    // =============================================================================
    // PROFILE ERRORS
    // =============================================================================
    
    /** Error when profile is not found */
    public static final String PROFILE_NOT_FOUND = "Profile not found for user: %s";
    
    /** Error when trying to access private profile */
    public static final String PROFILE_PRIVATE = "This profile is private";
    
    /** Error when trying to access students-only profile without auth */
    public static final String PROFILE_STUDENTS_ONLY = "This profile is only visible to authenticated students";
    
    // =============================================================================
    // VALIDATION ERRORS
    // =============================================================================
    
    /** Error when password is too short */
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 8 characters";
    
    /** Error when email domain is invalid */
    public static final String INVALID_EMAIL_DOMAIN = "Email must be a valid PSU address (@eng.psu.edu.eg)";
    
    /** Error when national ID format is invalid */
    public static final String INVALID_NATIONAL_ID = "National ID must be exactly 14 digits";
    
    /** Error when file type is not allowed */
    public static final String INVALID_FILE_TYPE = "File type not allowed. Allowed types: JPEG, PNG, GIF, WebP";
    
    /** Error when file size exceeds limit */
    public static final String FILE_TOO_LARGE = "File size exceeds maximum limit of 10 MB";
    
    /** Error when required field is missing */
    public static final String FIELD_REQUIRED = "%s is required";
    
    /** Error when field value is too long */
    public static final String FIELD_TOO_LONG = "%s must not exceed %d characters";
    
    // =============================================================================
    // ADMIN ERRORS
    // =============================================================================
    
    /** Error when trying to demote self */
    public static final String CANNOT_DEMOTE_SELF = "You cannot demote yourself";
    
    /** Error when user is already in requested state */
    public static final String USER_ALREADY_IN_STATE = "User is already in %s status";
    
    /** Error when email not verified for approval */
    public static final String EMAIL_NOT_VERIFIED_FOR_APPROVAL = "User's email must be verified before approval";
    
    // =============================================================================
    // CONTENT MODERATION ERRORS
    // =============================================================================
    
    /** Error when content contains banned words */
    public static final String CONTENT_CONTAINS_BANNED_WORDS = "Content contains prohibited words or phrases";
    
    /** Error when banned word already exists */
    public static final String BANNED_WORD_EXISTS = "Banned word already exists: %s";
    
    /** Error when banned word not found */
    public static final String BANNED_WORD_NOT_FOUND = "Banned word not found with ID: %d";
    
    // =============================================================================
    // FILE STORAGE ERRORS
    // =============================================================================
    
    /** Error when file upload fails */
    public static final String FILE_UPLOAD_FAILED = "Failed to upload file: %s";
    
    /** Error when file deletion fails */
    public static final String FILE_DELETE_FAILED = "Failed to delete file: %s";
    
    /** Error when storage service is unavailable */
    public static final String STORAGE_UNAVAILABLE = "File storage service is temporarily unavailable";
    
    // =============================================================================
    // RATE LIMITING ERRORS
    // =============================================================================
    
    /** Error when login rate limit is exceeded */
    public static final String LOGIN_RATE_LIMIT_EXCEEDED = "Too many login attempts. Please try again later.";
    
    /** Error when signup rate limit is exceeded */
    public static final String SIGNUP_RATE_LIMIT_EXCEEDED = "Too many registration attempts. Please try again later.";
    
    // =============================================================================
    // GENERIC ERRORS
    // =============================================================================
    
    /** Generic internal server error */
    public static final String INTERNAL_ERROR = "An unexpected error occurred. Please try again later.";
    
    /** Generic bad request error */
    public static final String BAD_REQUEST = "Invalid request. Please check your input and try again.";
    
    /** Generic resource not found */
    public static final String RESOURCE_NOT_FOUND = "The requested resource was not found";
}
