/**
 * Admin API module.
 * Provides administrative functions for user management, approval workflow, and email verification.
 * All endpoints require authentication with ADMIN role.
 * 
 * @module features/admin/admin.api
 */

import { apiFetch } from "../../core/api/http";

/**
 * Fetches all registered users in the system.
 * Admin-only endpoint.
 * 
 * @async
 * @function getAllUsers
 * @returns {Promise<Object>} Response with array of all users
 * @throws {Error} If unauthorized or server error occurs
 * 
 * @example
 * const response = await getAllUsers();
 * // response.data = [{ id: 1, email: '...', status: 'APPROVED', ... }, ...]
 */
export function getAllUsers() {
  return apiFetch("/api/admin/users", { method: "GET" }, { auth: true });
}

/**
 * Fetches users with PENDING approval status.
 * Admin-only endpoint for approval workflow.
 * 
 * @async
 * @function getPendingUsers
 * @returns {Promise<Object>} Response with array of pending users awaiting approval
 * @throws {Error} If unauthorized or server error occurs
 * 
 * @example
 * const response = await getPendingUsers();
 * // response.data = [{ id: 2, email: '...', status: 'PENDING', ... }, ...]
 */
export function getPendingUsers() {
  return apiFetch("/api/admin/users/pending", { method: "GET" }, { auth: true });
}

/**
 * Fetches detailed information for a specific user.
 * Admin-only endpoint.
 * 
 * @async
 * @function getUserDetails
 * @param {number} userId - The unique identifier of the user
 * @returns {Promise<Object>} Response with complete user details
 * @throws {Error} If user not found, unauthorized, or server error
 * 
 * @example
 * const response = await getUserDetails(5);
 * // response.data = { id: 5, email: '...', nationalId: '...', status: '...', ... }
 */
export function getUserDetails(userId) {
  return apiFetch(`/api/admin/users/${userId}`, { method: "GET" }, { auth: true });
}

/**
 * Fetches student profile information including bio, social links, and visibility settings.
 * 
 * @async
 * @function getStudentProfile
 * @param {number} userId - The unique identifier of the student
 * @returns {Promise<Object>} Response with student profile data
 * @throws {Error} If profile not found or server error
 * 
 * @example
 * const response = await getStudentProfile(5);
 * // response.data = { bio: '...', linkedin: '...', github: '...', visibility: 'PUBLIC', ... }
 */
export function getStudentProfile(userId) {
  return apiFetch(`/api/profile/${userId}`, { method: "GET" }, { auth: true });
}

/**
 * Approves or rejects a pending user registration.
 * Admin-only endpoint for user approval workflow.
 * 
 * @async
 * @function approveRejectUser
 * @param {Object} data - Approval decision data
 * @param {number} data.userId - ID of the user to approve/reject
 * @param {boolean} data.approved - true to approve, false to reject
 * @param {string} [data.rejectionReason] - Required if approved is false, reason for rejection
 * @returns {Promise<Object>} Response confirming the action
 * @throws {Error} If user not found, missing reason on rejection, or server error
 * 
 * @example
 * // Approve user
 * await approveRejectUser({ userId: 5, approved: true });
 * 
 * // Reject user with reason
 * await approveRejectUser({ 
 *   userId: 5, 
 *   approved: false, 
 *   rejectionReason: 'Invalid national ID document' 
 * });
 */
export function approveRejectUser(data) {
  return apiFetch("/api/admin/users/approve-reject", { 
    method: "POST", 
    body: JSON.stringify(data) 
  }, { auth: true });
}

/**
 * Sends email verification link to a user.
 * Admin-only endpoint. If backend is in testingMode, returns the token in response.
 * 
 * @async
 * @function sendVerification
 * @param {number} userId - ID of the user to send verification email to
 * @returns {Promise<Object>} Response confirming email sent (may include test token)
 * @throws {Error} If user not found, email already verified, or server error
 * 
 * @example
 * const response = await sendVerification(5);
 * // In testing mode: response.data = { message: '...', token: 'abc123...' }
 * // In production: response.data = { message: 'Verification email sent' }
 */
export function sendVerification(userId) {
  return apiFetch(`/api/admin/users/${userId}/send-verification`, { method: "POST" }, { auth: true });
}

/**
 * Manually verifies a user's email using a verification token.
 * Admin-only endpoint for manual verification (typically used in testing).
 * 
 * @async
 * @function verifyEmailWithToken
 * @param {number} userId - ID of the user to verify
 * @param {string} token - Email verification token
 * @returns {Promise<Object>} Response confirming email verification
 * @throws {Error} If token invalid, expired, or user not found
 * 
 * @example
 * await verifyEmailWithToken(5, 'abc123def456');
 */
export function verifyEmailWithToken(userId, token) {
  return apiFetch(`/api/admin/users/${userId}/verify-email/${token}`, { method: "POST" }, { auth: true });
}

/**
 * Changes a user's role between ADMIN and STUDENT.
 * Admin-only endpoint for role management.
 * 
 * @async
 * @function changeUserRole
 * @param {number} userId - ID of the user to update
 * @param {('ADMIN'|'STUDENT')} role - New role to assign
 * @returns {Promise<Object>} Response confirming role change
 * @throws {Error} If invalid role, user not found, or server error
 * 
 * @example
 * // Promote to admin
 * await changeUserRole(5, 'ADMIN');
 * 
 * // Demote to student
 * await changeUserRole(3, 'STUDENT');
 */
export function changeUserRole(userId, role) {
  return apiFetch(`/api/admin/users/${userId}/change-role`, {
    method: "POST",
    body: JSON.stringify({ role })
  }, { auth: true });
}