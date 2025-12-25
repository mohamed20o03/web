package com.abdelwahab.CampusCard.domain.common.constants;

import java.util.regex.Pattern;

/**
 * Central repository for all validation-related constants used across the application.
 * Ensures consistency in validation rules between backend and provides single source of truth
 * for all validation parameters.
 *
 * <p>Usage example:
 * <pre>{@code
 * if (password.length() < ValidationConstants.MIN_PASSWORD_LENGTH) {
 *     throw new ValidationException(ErrorMessages.PASSWORD_TOO_SHORT);
 * }
 * }</pre>
 *
 * @author CampusCard Team
 * @since 1.0
 */
public final class ValidationConstants {
    
    // Prevent instantiation
    private ValidationConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // =============================================================================
    // PASSWORD VALIDATION
    // =============================================================================
    
    /** Minimum password length requirement */
    public static final int MIN_PASSWORD_LENGTH = 8;
    
    /** Maximum password length allowed */
    public static final int MAX_PASSWORD_LENGTH = 100;
    
    // =============================================================================
    // TEXT FIELD LIMITS
    // =============================================================================
    
    /** Maximum length for user bio field */
    public static final int MAX_BIO_LENGTH = 500;
    
    /** Maximum length for interests field */
    public static final int MAX_INTERESTS_LENGTH = 500;
    
    /** Maximum length for first/last name */
    public static final int MAX_NAME_LENGTH = 50;
    
    /** Maximum length for email address */
    public static final int MAX_EMAIL_LENGTH = 255;
    
    // =============================================================================
    // FILE UPLOAD LIMITS
    // =============================================================================
    
    /** Maximum file size in bytes (10 MB) */
    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;
    
    /** Maximum file size in megabytes */
    public static final int MAX_FILE_SIZE_MB = 10;
    
    /** Allowed image MIME types for uploads */
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    };
    
    // =============================================================================
    // PATTERNS
    // =============================================================================
    
    /** Required email domain for PSU students */
    public static final String EMAIL_DOMAIN = "@eng.psu.edu.eg";
    
    /** Pattern for 14-digit Egyptian National ID */
    public static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^\\d{14}$");
    
    /** Pattern for valid phone number (international format) */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,20}$");
    
    /** Pattern for valid LinkedIn profile URL */
    public static final Pattern LINKEDIN_PATTERN = Pattern.compile(
        "^(https?://)?(www\\.)?linkedin\\.com/.*$"
    );
    
    /** Pattern for valid GitHub profile URL */
    public static final Pattern GITHUB_PATTERN = Pattern.compile(
        "^(https?://)?(www\\.)?github\\.com/.*$"
    );
    
    /** Pattern for valid visibility values */
    public static final Pattern VISIBILITY_PATTERN = Pattern.compile(
        "^(PUBLIC|STUDENTS_ONLY|PRIVATE)$",
        Pattern.CASE_INSENSITIVE
    );
    
    // =============================================================================
    // PAGINATION DEFAULTS
    // =============================================================================
    
    /** Default page size for paginated queries */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /** Maximum page size allowed */
    public static final int MAX_PAGE_SIZE = 100;
    
    // =============================================================================
    // RATE LIMITING DEFAULTS
    // =============================================================================
    
    /** Default login rate limit (attempts per window) */
    public static final int DEFAULT_LOGIN_RATE_LIMIT = 5;
    
    /** Default login rate limit window in minutes */
    public static final int DEFAULT_LOGIN_WINDOW_MINUTES = 15;
    
    /** Default signup rate limit (attempts per window) */
    public static final int DEFAULT_SIGNUP_RATE_LIMIT = 3;
    
    /** Default signup rate limit window in minutes */
    public static final int DEFAULT_SIGNUP_WINDOW_MINUTES = 60;
}
