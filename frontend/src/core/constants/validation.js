/**
 * Central repository for all validation-related constants used in the frontend.
 * Ensures consistency with backend validation rules.
 *
 * @module constants/validation
 * @author CampusCard Team
 * @since 1.0
 */

// =============================================================================
// PASSWORD VALIDATION
// =============================================================================

/** Minimum password length requirement */
export const MIN_PASSWORD_LENGTH = 8;

/** Maximum password length allowed */
export const MAX_PASSWORD_LENGTH = 100;

// =============================================================================
// TEXT FIELD LIMITS
// =============================================================================

/** Maximum length for user bio field */
export const MAX_BIO_LENGTH = 500;

/** Maximum length for interests field */
export const MAX_INTERESTS_LENGTH = 500;

/** Maximum length for first/last name */
export const MAX_NAME_LENGTH = 50;

/** Maximum length for email address */
export const MAX_EMAIL_LENGTH = 255;

// =============================================================================
// FILE UPLOAD LIMITS
// =============================================================================

/** Maximum file size in bytes (10 MB) */
export const MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

/** Maximum file size in megabytes */
export const MAX_FILE_SIZE_MB = 10;

/** Allowed image MIME types for uploads */
export const ALLOWED_IMAGE_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp'
];

/** Allowed image file extensions */
export const ALLOWED_IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];

// =============================================================================
// PATTERNS
// =============================================================================

/** Required email domain for PSU students */
export const EMAIL_DOMAIN = '@eng.psu.edu.eg';

/** Regex pattern for valid PSU email */
export const EMAIL_PATTERN = /^[a-zA-Z0-9._%+-]+@eng\.psu\.edu\.eg$/;

/** Pattern for 14-digit Egyptian National ID */
export const NATIONAL_ID_PATTERN = /^\d{14}$/;

/** Pattern for valid phone number (international format) */
export const PHONE_PATTERN = /^[+]?[0-9]{10,20}$/;

/** Pattern for valid LinkedIn profile URL */
export const LINKEDIN_PATTERN = /^(https?:\/\/)?(www\.)?linkedin\.com\/.+$/;

/** Pattern for valid GitHub profile URL */
export const GITHUB_PATTERN = /^(https?:\/\/)?(www\.)?github\.com\/.+$/;

// =============================================================================
// VISIBILITY OPTIONS
// =============================================================================

/** Available profile visibility options */
export const VISIBILITY_OPTIONS = {
  PUBLIC: 'PUBLIC',
  STUDENTS_ONLY: 'STUDENTS_ONLY',
  PRIVATE: 'PRIVATE'
};

/** Visibility display labels */
export const VISIBILITY_LABELS = {
  PUBLIC: 'Public - Visible to everyone',
  STUDENTS_ONLY: 'Students Only - Visible to authenticated students',
  PRIVATE: 'Private - Visible only to you and admins'
};

// =============================================================================
// USER STATUS
// =============================================================================

/** User approval status values */
export const USER_STATUS = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED'
};

/** User role values */
export const USER_ROLES = {
  STUDENT: 'STUDENT',
  ADMIN: 'ADMIN'
};

// =============================================================================
// PAGINATION DEFAULTS
// =============================================================================

/** Default page size for paginated lists */
export const DEFAULT_PAGE_SIZE = 20;

/** Maximum page size allowed */
export const MAX_PAGE_SIZE = 100;

// =============================================================================
// UI CONSTANTS
// =============================================================================

/** Debounce delay for search inputs (ms) */
export const SEARCH_DEBOUNCE_MS = 300;

/** Toast notification duration (ms) */
export const TOAST_DURATION_MS = 5000;

/** API request timeout (ms) */
export const API_TIMEOUT_MS = 30000;

/** Local storage keys */
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'campuscard_token',
  USER_DATA: 'campuscard_user',
  THEME: 'campuscard_theme'
};
