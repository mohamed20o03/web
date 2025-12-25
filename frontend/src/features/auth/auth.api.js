/**
 * Authentication API module.
 * Provides functions for user authentication, registration, and profile photo uploads.
 * 
 * @module features/auth/auth.api
 */

import { apiFetch } from "../../core/api/http";

/**
 * Authenticates a user with email/nationalId and password.
 * Public endpoint - no authentication required.
 * 
 * @async
 * @function loginRequest
 * @param {Object} credentials - Login credentials
 * @param {string} credentials.identifier - User's email address or national ID
 * @param {string} credentials.password - User's password
 * @returns {Promise<Object>} Response object containing JWT token and user data
 * @throws {Error} If credentials are invalid or server error occurs
 * 
 * @example
 * const response = await loginRequest({
 *   identifier: 'student@eng.psu.edu.eg',
 *   password: 'SecurePass123'
 * });
 * // response.data = { token: 'jwt...', email: '...', role: 'STUDENT' }
 */
export function loginRequest({ identifier, password }) {
  return apiFetch(
    "/api/login",
    {
      method: "POST",
      body: JSON.stringify({ identifier, password }),
    },
    { auth: false }
  );
}

/**
 * Registers a new user account with profile information.
 * Public endpoint - sends multipart form data including optional profile photo.
 * 
 * @async
 * @function signupRequest
 * @param {FormData} formData - Form data containing user registration information
 * @param {string} formData.email - User's university email (must be @eng.psu.edu.eg)
 * @param {string} formData.password - User's password (min 8 characters)
 * @param {string} formData.nationalId - User's 14-digit national ID
 * @param {string} formData.fullName - User's full name
 * @param {string} formData.faculty - Faculty name
 * @param {string} formData.department - Department name
 * @param {File} [formData.profilePhoto] - Optional profile photo file
 * @returns {Promise<Object>} Response with registration success message
 * @throws {Error} If validation fails, email exists, or server error occurs
 * 
 * @example
 * const formData = new FormData();
 * formData.append('email', 'newstudent@eng.psu.edu.eg');
 * formData.append('password', 'SecurePass123');
 * formData.append('nationalId', '30303130300275');
 * formData.append('fullName', 'Ahmed Mohamed');
 * formData.append('faculty', 'Engineering');
 * formData.append('department', 'Computer Science');
 * 
 * const response = await signupRequest(formData);
 */
export function signupRequest(formData) {
  return apiFetch(
    "/api/signup",
    {
      method: "POST",
      body: formData,
    },
    { auth: false }
  );
}

/**
 * Uploads or updates the user's profile photo.
 * Requires authentication - JWT token must be in storage.
 * 
 * @async
 * @function uploadProfilePhoto
 * @param {File} file - Image file to upload (JPEG, PNG)
 * @returns {Promise<Object>} Response with uploaded photo URL
 * @throws {Error} If file is invalid, too large, or upload fails
 * 
 * @example
 * const fileInput = document.querySelector('#photoInput');
 * const file = fileInput.files[0];
 * 
 * const response = await uploadProfilePhoto(file);
 * // response.data = { url: 'https://minio.../photo.jpg' }
 */
export function uploadProfilePhoto(file) {
  const fd = new FormData();
  fd.append("file", file);
  return apiFetch("/api/profile/photo", { method: "POST", body: fd }, { auth: true });
}

/**
 * Uploads the user's national ID scan document.
 * Optional endpoint for additional verification if implemented on backend.
 * Requires authentication - JWT token must be in storage.
 * 
 * @async
 * @function uploadNationalIdScan
 * @param {File} file - National ID scan image file
 * @returns {Promise<Object>} Response with uploaded document URL
 * @throws {Error} If file is invalid or upload fails
 * 
 * @example
 * const scanFile = document.querySelector('#idScanInput').files[0];
 * const response = await uploadNationalIdScan(scanFile);
 */
export function uploadNationalIdScan(file) {
  const fd = new FormData();
  fd.append("file", file);
  return apiFetch("/api/profile/national-id-scan", { method: "POST", body: fd }, { auth: true });
}

