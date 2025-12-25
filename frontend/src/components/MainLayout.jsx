/**
 * Main layout component with navigation bar and content outlet.
 * Provides consistent navigation across all authenticated and public pages.
 * 
 * @module components/MainLayout
 * @component
 * 
 * Features:
 * - Fixed glass morphism navigation bar
 * - Role-based navigation links (Admin/Student/Public)
 * - User profile dropdown with logout
 * - Responsive design
 * - Active route highlighting
 * 
 * @example
 * <Route element={<MainLayout />}>
 *   <Route path="/" element={<HomePage />} />
 *   <Route path="/me" element={<ProfilePage />} />
 * </Route>
 * 
 * @returns {JSX.Element} Layout with navigation and content outlet
 */

import React from "react";
import { Outlet, Link, useNavigate, useLocation } from "react-router-dom";
import { authStorage } from "../core/auth/auth.storage";
import logo from "../assets/psu-logo.png";

/**
 * MainLayout - Application layout with navigation.
 * 
 * @component
 */
export default function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = authStorage.getUser();
  const role = authStorage.getRole();

  /**
   * Handles user logout.
   * Clears session storage and navigates to login page.
   */
  function handleLogout() {
    authStorage.clear();
    navigate("/login");
  }

  /**
   * Checks if the given path matches current location.
   * 
   * @param {string} path - Path to check
   * @returns {boolean} True if path is active
   */
  const isActive = (path) => location.pathname === path;

  // Display name, email prefix, or 'User' as fallback
  const displayName = user?.firstName || user?.email?.split("@")[0] || "User";

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        minHeight: "100vh",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      {/* === GLASS NAVBAR === */}
      <nav
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          height: "80px",
          zIndex: 1000,
          background: "rgba(15, 23, 42, 0.7)", // Transparent dark background
          backdropFilter: "blur(12px)",
          WebkitBackdropFilter: "blur(12px)",
          borderBottom: "1px solid rgba(255, 255, 255, 0.1)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "0 40px",
        }}
      >
        {/* Logo */}
        <Link
          to="/"
          style={{
            display: "flex",
            alignItems: "center",
            gap: 12,
            textDecoration: "none",
          }}
        >
          <img src={logo} alt="PSU" style={{ height: 45 }} />
          <div style={{ display: "flex", flexDirection: "column" }}>
            <span style={{ color: "white", fontWeight: "700", fontSize: 20 }}>
              Campus Card
            </span>
            <span
              style={{ color: "#94a3b8", fontSize: 11, letterSpacing: "1px" }}
            >
              PSU ENGINEERING
            </span>
          </div>
        </Link>

        {/* Links */}
        <div style={{ display: "flex", gap: 30, alignItems: "center" }}>
          <Link to="/" style={isActive("/") ? activeLinkStyle : linkStyle}>
            Directory
          </Link>
          {user && (role === "ADMIN" || role === "ROLE_ADMIN") && (
            <Link
              to="/admin"
              style={isActive("/admin") ? activeLinkStyle : linkStyle}
              title="Dashboard"
            >
              <span
                style={{
                  fontSize: 20,
                  verticalAlign: "middle",
                  marginRight: 4,
                  display: "inline-block",
                  lineHeight: 1,
                }}
              >
                🏠
              </span>
              Dashboard
            </Link>
          )}
          {user && (role === "ADMIN" || role === "ROLE_ADMIN") && (
            <Link
              to="/admin/users"
              style={isActive("/admin/users") ? activeLinkStyle : linkStyle}
              title="Manage Users"
            >
              <span
                style={{
                  fontSize: 18,
                  verticalAlign: "middle",
                  marginRight: 4,
                }}
              >
                👤
              </span>{" "}
              Users
            </Link>
          )}
          {user && (
            <Link
              to="/me"
              style={isActive("/me") ? activeLinkStyle : linkStyle}
            >
              My Profile
            </Link>
          )}
          {user ? (
            <div style={{ display: "flex", alignItems: "center", gap: 15 }}>
              <span style={{ color: "white", fontSize: 14, fontWeight: 500 }}>
                Hi, {displayName}
              </span>
              <button onClick={handleLogout} style={logoutBtnStyle}>
                Logout
              </button>
            </div>
          ) : (
            <div style={{ display: "flex", gap: 15 }}>
              <Link
                to="/login"
                style={{
                  ...linkStyle,
                  border: isActive("/login")
                    ? "1px solid rgba(255,255,255,0.5)"
                    : "transparent",
                  borderRadius: 20,
                  padding: "8px 20px",
                }}
              >
                Login
              </Link>
              <Link to="/signup" style={signupBtnStyle}>
                Sign Up
              </Link>
            </div>
          )}
        </div>
      </nav>

      {/* Page Content */}
      <div style={{ flex: 1 }}>
        <Outlet />
      </div>
    </div>
  );
}

// Styles
const linkStyle = {
  color: "#94a3b8",
  textDecoration: "none",
  fontSize: 15,
  fontWeight: 500,
  transition: "0.3s",
};
const activeLinkStyle = { ...linkStyle, color: "white", fontWeight: "bold" };
const logoutBtnStyle = {
  background: "rgba(239, 68, 68, 0.15)",
  color: "#f87171",
  border: "1px solid rgba(239, 68, 68, 0.3)",
  padding: "8px 18px",
  borderRadius: 20,
  cursor: "pointer",
  fontWeight: "600",
};
const signupBtnStyle = {
  background: "linear-gradient(90deg, #00c6ff 0%, #0072ff 100%)",
  color: "white",
  padding: "8px 25px",
  borderRadius: 20,
  textDecoration: "none",
  fontWeight: "600",
  fontSize: 14,
};
