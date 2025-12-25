import React from "react";
import { Navigate } from "react-router-dom";
import { authStorage } from "./auth.storage";

export function RequireApprovedStudent({ children }) {
  const session = authStorage.get();

  if (!session?.token) {
    return <Navigate to="/login" replace />;
  }

  const role = (session.role || "").toLowerCase();
  const status = (session.status || "").toUpperCase();

  // Admin users have full access
  if (role === "admin") return children;

  // Students must have APPROVED status
  if (status !== "APPROVED") {
    return <Navigate to="/status" replace />;
  }

  return children;
}
