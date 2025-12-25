/**
 * Public API module.
 * Provides functions for accessing public student profiles and academic data.
 * These endpoints do not require authentication.
 * 
 * @module features/public/public.api
 */

import { apiRequest } from "../../core/api/apiClient";

/**
 * Fetches all approved students with PUBLIC visibility.
 * Public endpoint - no authentication required.
 * Returns students who have been approved by admin and set their profile to public.
 * 
 * @async
 * @function getPublicUsers
 * @returns {Promise<Array>} Array of public student profile objects
 * @throws {Error} If server error occurs
 * 
 * @example
 * const students = await getPublicUsers();
 * // students = [{ id: 1, fullName: '...', faculty: '...', department: '...', ... }, ...]
 */
export function getPublicUsers() {
  return apiRequest("/api/profile/public-students");
}

/**
 * Fetches detailed profile information for a specific user.
 * Public endpoint if user has public visibility, otherwise requires authentication.
 * 
 * @async
 * @function getPublicProfile
 * @param {number} userId - The unique identifier of the user
 * @returns {Promise<Object>} User profile with bio, social links, and contact info
 * @throws {Error} If user not found or profile is private and not authenticated
 * 
 * @example
 * const profile = await getPublicProfile(5);
 * // profile = { fullName: '...', bio: '...', linkedin: '...', github: '...', ... }
 */
export function getPublicProfile(userId) {
  return apiRequest(`/api/profile/${userId}`);
}

/**
 * Fetches list of all faculties.
 * Public endpoint for registration and filtering.
 * 
 * @async
 * @function getFaculties
 * @returns {Promise<Array>} Array of faculty objects with id and name
 * @throws {Error} If server error occurs
 * 
 * @example
 * const faculties = await getFaculties();
 * // faculties = [{ id: 1, name: 'Engineering' }, { id: 2, name: 'Science' }, ...]
 */
export function getFaculties() {
  return apiRequest("/api/public/faculties", {}, { auth: false });
}

/**
 * Fetches list of departments, optionally filtered by faculty.
 * Public endpoint for registration and filtering.
 * 
 * @async
 * @function getDepartments
 * @param {number} [facultyId=null] - Optional faculty ID to filter departments
 * @returns {Promise<Array>} Array of department objects with id and name
 * @throws {Error} If server error occurs
 * 
 * @example
 * // Get all departments
 * const allDepts = await getDepartments();
 * 
 * // Get departments for specific faculty
 * const engineeringDepts = await getDepartments(1);
 * // engineeringDepts = [{ id: 1, name: 'Computer Science' }, { id: 2, name: 'Electrical' }, ...]
 */
export function getDepartments(facultyId = null) {
  const url = facultyId
    ? `/api/public/departments?facultyId=${facultyId}`
    : "/api/public/departments";
  return apiRequest(url, {}, { auth: false });
}

