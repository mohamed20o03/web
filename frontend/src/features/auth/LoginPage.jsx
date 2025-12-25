/**
 * Login page component for user authentication.
 * Supports login via email or national ID with password.
 * Handles role-based navigation after successful authentication.
 * 
 * @module features/auth/LoginPage
 * @component
 * 
 * @example
 * <Route path="/login" element={<LoginPage />} />
 * 
 * @returns {JSX.Element} Login page with authentication form
 */

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import bg from "../../assets/login-bg.jpg";
import { authStorage } from "../../core/auth/auth.storage";
import { useAuth } from "../../core/auth/auth.context";
import { loginRequest } from "./auth.api";
import { pageBackground, centerContainer } from "../../core/theme";

/**
 * LoginPage - User authentication page.
 * 
 * Features:
 * - Email/National ID authentication
 * - Password visibility toggle
 * - Role-based navigation (Admin → dashboard, Student → profile/pending)
 * - Glass morphism design with background image
 * - Error message display
 * - Session management
 * 
 * @component
 */
export default function LoginPage() {
  const navigate = useNavigate();
  const { setSession } = useAuth();

  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  /**
   * Handles form submission and authentication.
   * Navigates based on user role and status after successful login.
   * 
   * @async
   * @param {Event} e - Form submit event
   */
  async function onSubmit(e) {
    e.preventDefault();
    setErrorMsg("");
    setLoading(true);

    const result = await loginRequest({ identifier, password });
    setLoading(false);

    if (!result.ok) {
      setErrorMsg(result.error?.message || "Login failed");
      return;
    }

    const data = result.data;
    const session = {
      token: data.token,
      userId: data.id,
      email: data.email,
      firstName: data.firstName,
      role: data.role,
      status: data.status,
    };

    setSession(session);
    authStorage.set(session);

    // Role-based navigation
    if (String(data.role).toUpperCase() === "ADMIN") {
      navigate("/admin/users", { replace: true });
    } else if (data.status !== "APPROVED") {
      navigate("/status", { replace: true });
    } else {
      navigate("/me", { replace: true });
    }
  }

  return (
    <div
      className="min-h-screen flex items-center justify-center pt-20"
      style={{
        ...pageBackground(bg),
        backgroundImage: `linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.7)), url(${bg})`,
      }}
    >
      {/* Glass Card */}
      <div className="glass-card w-full max-w-md mx-4" style={{ padding: "50px" }}>
        <div className="text-center mb-8">
          <h2 className="text-white text-3xl font-bold mb-2">
            Welcome Back
          </h2>
          <p className="text-slate-400">
            Login with your university account.
          </p>
        </div>

        {errorMsg && (
          <div
            className="mb-5 p-3 rounded-lg text-center text-sm border"
            style={{
              background: "rgba(239, 68, 68, 0.15)",
              color: "#fca5a5",
              borderColor: "rgba(239, 68, 68, 0.2)",
            }}
          >
            ⚠️ {errorMsg}
          </div>
        )}

        <form onSubmit={onSubmit} className="flex flex-col gap-5">
          <div>
            <label className="block text-slate-300 mb-2 text-sm">
              Email
            </label>
            <input
              type="email"
              required
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              placeholder="student@eng.psu.edu.eg"
              className="w-full px-4 py-3 rounded-xl text-white text-base outline-none input-dark"
            />
          </div>

          <div>
            <label className="block text-slate-300 mb-2 text-sm">
              Password
            </label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full px-4 py-3 pr-12 rounded-xl text-white text-base outline-none input-dark"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 bg-transparent border-none cursor-pointer text-xl text-slate-400"
              >
                {showPassword ? "🙈" : "👁️"}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="mt-2 py-3.5 px-4 rounded-xl text-white text-base font-bold cursor-pointer border-none"
            style={{
              background: loading
                ? "rgba(100, 116, 139, 0.5)"
                : "linear-gradient(90deg, #00c6ff 0%, #0072ff 100%)",
              boxShadow: loading ? "none" : "0 4px 15px rgba(0, 114, 255, 0.3)",
              cursor: loading ? "not-allowed" : "pointer",
            }}
          >
            {loading ? "Logging in..." : "Log In"}
          </button>
        </form>

        <div className="mt-6 text-center text-slate-400 text-sm">
          Don't have an account?{" "}
          <Link
            to="/signup"
            className="text-sky-400 font-bold no-underline hover:text-sky-300"
          >
            Register Now
          </Link>
        </div>
      </div>
    </div>
  );
}
