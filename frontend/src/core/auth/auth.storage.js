/**
 * Authentication storage module.
 * Manages user session data in localStorage with safe parsing and helper methods.
 * 
 * @module core/auth/auth.storage
 */

const KEY = "campuscard.session";

/**
 * Authentication storage interface.
 * Provides methods to manage user session data in localStorage.
 * 
 * @namespace authStorage
 * 
 * @example
 * // Store session after login
 * authStorage.set({ token: 'jwt...', email: 'user@...', role: 'STUDENT' });
 * 
 * // Retrieve session
 * const session = authStorage.get();
 * console.log(session.token);
 * 
 * // Clear session on logout
 * authStorage.clear();
 * 
 * // Helper methods
 * const token = authStorage.getToken();
 * const role = authStorage.getRole();
 */
export const authStorage = {
  /**
   * Retrieves the current user session from localStorage.
   * Returns null if no session exists or if parsing fails.
   * 
   * @function get
   * @returns {Object|null} Session object containing token, email, role, etc., or null
   * 
   * @example
   * const session = authStorage.get();
   * if (session) {
   *   console.log('Logged in as:', session.email);
   * }
   */
  get() {
    try {
      const raw = localStorage.getItem(KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  /**
   * Stores user session data in localStorage.
   * Automatically serializes the session object to JSON.
   * 
   * @function set
   * @param {Object} session - Session data to store
   * @param {string} session.token - JWT authentication token
   * @param {string} session.email - User's email address
   * @param {('ADMIN'|'STUDENT')} session.role - User's role
   * 
   * @example
   * authStorage.set({
   *   token: 'eyJhbGciOiJIUzI1NiIs...',
   *   email: 'student@eng.psu.edu.eg',
   *   role: 'STUDENT'
   * });
   */
  set(session) {
    localStorage.setItem(KEY, JSON.stringify(session));
  },

  /**
   * Clears the user session from localStorage.
   * Called on logout or when a 401 Unauthorized response is received.
   * 
   * @function clear
   * 
   * @example
   * // On logout
   * authStorage.clear();
   * navigate('/login');
   */
  clear() {
    localStorage.removeItem(KEY);
  },

  /**
   * Helper method to retrieve only the JWT token.
   * Returns undefined if no session exists.
   * 
   * @function getToken
   * @returns {string|undefined} JWT token or undefined
   * 
   * @example
   * const token = authStorage.getToken();
   * if (token) {
   *   // User is authenticated
   * }
   */
  getToken() {
    const session = this.get();
    return session?.token;
  },

  /**
   * Helper method to retrieve only the user's role.
   * Returns undefined if no session exists.
   * 
   * @function getRole
   * @returns {('ADMIN'|'STUDENT')|undefined} User role or undefined
   * 
   * @example
   * const role = authStorage.getRole();
   * if (role === 'ADMIN') {
   *   // Show admin features
   * }
   */
  getRole() {
    const session = this.get();
    return session?.role;
  },

  /**
   * Helper method to retrieve the entire user session.
   * Alias for get() method for semantic clarity.
   * 
   * @function getUser
   * @returns {Object|null} Session object or null
   * 
   * @example
   * const user = authStorage.getUser();
   * console.log(`Welcome, ${user?.email}`);
   */
  getUser() {
    const session = this.get();
    return session; 
  }
};
