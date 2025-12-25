/**
 * HTTP client module for making API requests.
 * Handles authentication, JSON parsing, error handling, and session management.
 * 
 * @module core/api/http
 */

import { env } from "../config/env";
import { authStorage } from "../auth/auth.storage";
import { toApiError } from "./errors";

/**
 * Safely parses JSON text without throwing exceptions.
 * Returns the parsed object or a raw text wrapper if parsing fails.
 * 
 * @private
 * @function safeJson
 * @param {string} text - JSON text to parse
 * @returns {Object} Parsed JSON object or { raw: text } if invalid
 */
function safeJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return { raw: text };
  }
}

/**
 * Performs an API request with automatic authentication and error handling.
 * 
 * Features:
 * - Automatic JWT token injection from session storage
 * - Automatic Content-Type: application/json for non-FormData requests
 * - Session clearing on 401 Unauthorized
 * - Standardized error response format
 * - Network error handling
 * 
 * @async
 * @function apiFetch
 * @param {string} path - API endpoint path (e.g., '/api/login')
 * @param {Object} [init={}] - Fetch init options (method, body, headers)
 * @param {string} [init.method='GET'] - HTTP method
 * @param {(string|FormData)} [init.body] - Request body
 * @param {Object} [init.headers] - Additional headers
 * @param {Object} [opts={}] - Additional options
 * @param {boolean} [opts.auth=true] - Whether to include auth token
 * @returns {Promise<Object>} Response object { ok: boolean, data?: Object, error?: Object }
 * 
 * @example
 * // Authenticated request
 * const response = await apiFetch('/api/profile', { method: 'GET' });
 * if (response.ok) {
 *   console.log(response.data);
 * } else {
 *   console.error(response.error.message);
 * }
 * 
 * @example
 * // Public request without authentication
 * const response = await apiFetch('/api/login', {
 *   method: 'POST',
 *   body: JSON.stringify({ email: '...', password: '...' })
 * }, { auth: false });
 * 
 * @example
 * // FormData upload with authentication
 * const formData = new FormData();
 * formData.append('file', fileInput.files[0]);
 * const response = await apiFetch('/api/profile/photo', {
 *   method: 'POST',
 *   body: formData
 * }, { auth: true });
 */
export async function apiFetch(path, init = {}, opts = { auth: true }) {
  const url = `${env.API_BASE_URL}${path}`;
  const session = authStorage.get();
  const needsAuth = opts?.auth !== false;

  const headers = { ...(init.headers || {}) };
  const isFormData = init.body instanceof FormData;

  if (!isFormData && init.body && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  if (needsAuth && session?.token) {
    headers["Authorization"] = `Bearer ${session.token}`;
  }

  try {
    const res = await fetch(url, { ...init, headers });

    const text = await res.text();
    const body = text ? safeJson(text) : null;

    if (!res.ok) {
      if (res.status === 401) authStorage.clear();
      return { ok: false, error: toApiError(res.status, body) };
    }

    return { ok: true, data: body };
  } catch {
    return { ok: false, error: { kind: "network", message: "Network error" } };
  }
}

