/**
 * Authentication context for managing user session state.
 * Provides session management and authentication status across the application.
 * 
 * @module core/auth/auth.context
 */

/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useCallback, useContext, useMemo, useState } from "react";
import { authStorage } from "./auth.storage";

const AuthContext = createContext(null);

/**
 * Authentication provider component.
 * Manages user session state and provides authentication methods.
 * 
 * @component
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components
 * 
 * @example
 * <AuthProvider>
 *   <App />
 * </AuthProvider>
 */
export function AuthProvider({ children }) {
  const [session, setSessionState] = useState(() => authStorage.get());

  /**
   * Updates the session state and persists to storage.
   * 
   * @param {Object|null} s - Session object or null to clear
   */
  const setSession = useCallback((s) => {
    setSessionState(s);
    if (s) authStorage.set(s);
    else authStorage.clear();
  }, []);

  /**
   * Logs out the current user by clearing the session.
   */
  const logout = useCallback(() => {
    setSession(null);
  }, [setSession]);

  const value = useMemo(() => ({ session, setSession, logout }), [session, setSession, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * Hook to access authentication context.
 * Must be used within AuthProvider.
 * 
 * @function useAuth
 * @returns {Object} Authentication context
 * @returns {Object|null} returns.session - Current user session
 * @returns {Function} returns.setSession - Function to update session
 * @returns {Function} returns.logout - Function to logout user
 * 
 * @throws {Error} If used outside AuthProvider
 * 
 * @example
 * function MyComponent() {
 *   const { session, logout } = useAuth();
 *   
 *   if (!session) return <div>Not logged in</div>;
 *   
 *   return (
 *     <div>
 *       <p>Welcome {session.email}</p>
 *       <button onClick={logout}>Logout</button>
 *     </div>
 *   );
 * }
 */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
