/**
 * Central repository for all user-facing error messages in the frontend.
 * Provides consistent, localized error messaging across the application.
 *
 * @module constants/errors
 * @author CampusCard Team
 * @since 1.0
 */

// =============================================================================
// AUTHENTICATION ERRORS
// =============================================================================

/** Error when login credentials are invalid */
export const INVALID_CREDENTIALS = 'Invalid email or password';

/** Error when user account is not approved yet */
export const ACCOUNT_NOT_APPROVED = 'Your account is pending approval by an administrator';

/** Error when user account has been rejected */
export const ACCOUNT_REJECTED = 'Your account has been rejected';

/** Error when email is not verified */
export const EMAIL_NOT_VERIFIED = 'Please verify your email address before logging in';

/** Error when session has expired */
export const SESSION_EXPIRED = 'Your session has expired. Please log in again.';

/** Error when user lacks required permissions */
export const ACCESS_DENIED = "You don't have permission to access this page";

// =============================================================================
// VALIDATION ERRORS
// =============================================================================

/** Error when password is too short */
export const PASSWORD_TOO_SHORT = 'Password must be at least 8 characters';

/** Error when passwords don't match */
export const PASSWORDS_DONT_MATCH = "Passwords don't match";

/** Error when email domain is invalid */
export const INVALID_EMAIL_DOMAIN = 'Email must be a valid PSU address (@eng.psu.edu.eg)';

/** Error when email format is invalid */
export const INVALID_EMAIL_FORMAT = 'Please enter a valid email address';

/** Error when national ID format is invalid */
export const INVALID_NATIONAL_ID = 'National ID must be exactly 14 digits';

/** Error when required field is missing */
export const FIELD_REQUIRED = 'This field is required';

/** Error when field value is too long */
export const FIELD_TOO_LONG = 'This field exceeds the maximum allowed length';

// =============================================================================
// FILE UPLOAD ERRORS
// =============================================================================

/** Error when file type is not allowed */
export const INVALID_FILE_TYPE = 'File type not allowed. Please upload JPEG, PNG, GIF, or WebP images.';

/** Error when file size exceeds limit */
export const FILE_TOO_LARGE = 'File size exceeds maximum limit of 10 MB';

/** Error when file upload fails */
export const FILE_UPLOAD_FAILED = 'Failed to upload file. Please try again.';

/** Error when no file is selected */
export const NO_FILE_SELECTED = 'Please select a file to upload';

// =============================================================================
// PROFILE ERRORS
// =============================================================================

/** Error when profile is not found */
export const PROFILE_NOT_FOUND = 'Profile not found';

/** Error when trying to access private profile */
export const PROFILE_PRIVATE = 'This profile is private';

/** Error when profile update fails */
export const PROFILE_UPDATE_FAILED = 'Failed to update profile. Please try again.';

// =============================================================================
// NETWORK ERRORS
// =============================================================================

/** Error when network request fails */
export const NETWORK_ERROR = 'Unable to connect to server. Please check your internet connection.';

/** Error when server returns 500 */
export const SERVER_ERROR = 'An unexpected server error occurred. Please try again later.';

/** Error when request times out */
export const REQUEST_TIMEOUT = 'Request timed out. Please try again.';

/** Error when API is unavailable */
export const SERVICE_UNAVAILABLE = 'Service is temporarily unavailable. Please try again later.';

// =============================================================================
// RATE LIMITING ERRORS
// =============================================================================

/** Error when login rate limit is exceeded */
export const LOGIN_RATE_LIMIT = 'Too many login attempts. Please try again in 15 minutes.';

/** Error when signup rate limit is exceeded */
export const SIGNUP_RATE_LIMIT = 'Too many registration attempts. Please try again later.';

// =============================================================================
// GENERIC ERRORS
// =============================================================================

/** Generic error for unknown issues */
export const GENERIC_ERROR = 'Something went wrong. Please try again.';

/** Error when action cannot be performed */
export const ACTION_FAILED = 'Unable to complete this action. Please try again.';

/** Error when data cannot be loaded */
export const LOAD_FAILED = 'Failed to load data. Please refresh the page.';

// =============================================================================
// SUCCESS MESSAGES
// =============================================================================

/** Success message for login */
export const LOGIN_SUCCESS = 'Welcome back!';

/** Success message for registration */
export const SIGNUP_SUCCESS = 'Registration successful! Please check your email to verify your account.';

/** Success message for profile update */
export const PROFILE_UPDATED = 'Profile updated successfully';

/** Success message for photo upload */
export const PHOTO_UPLOADED = 'Photo uploaded successfully';

/** Success message for password change */
export const PASSWORD_CHANGED = 'Password changed successfully';

/** Success message for email verification */
export const EMAIL_VERIFIED = 'Email verified successfully! You can now log in.';
