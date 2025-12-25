//eslint-disable jsx-a11y/alt-text
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authStorage } from "../../core/auth/auth.storage";
import { apiFetch } from "../../core/api/http";
import styles from "./LoginPage.module.css"; // Make sure this file exists
import bg from "../../assets/login-bg.jpg";
import logo from "../../assets/psu-logo.png";

// ✅ Banned words are now managed in the backend
// Content moderation is handled server-side

export default function PendingStatusPage() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Form States
  const [bio, setBio] = useState("");
  const [linkedin, setLinkedin] = useState("");
  const [github, setGithub] = useState("");
  const [profilePhoto, setProfilePhoto] = useState(""); // Correct field name for backend

  const navigate = useNavigate();
  const [status, setStatus] = useState("");

  useEffect(() => {
    loadProfile();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (status && status.toUpperCase() === "REJECTED") {
      navigate("/me", { replace: true });
    }
  }, [status, navigate]);

  async function loadProfile() {
    setLoading(true);
    // Fetch current data (if the student previously filled it out)
    const res = await apiFetch(
      "/api/profile",
      { method: "GET" },
      { auth: true }
    );
    setLoading(false);
    if (res.ok && res.data) {
      const p = res.data;
      setBio(p.bio || "");
      setLinkedin(p.linkedin || "");
      setGithub(p.github || "");
      setProfilePhoto(p.profilePhoto || "");
      setStatus(p.status || "");
    }
  }

  function handleLogout() {
    authStorage.clear();
    window.location.href = "/login";
  }

  // ✅ Content moderation is now handled by the backend
  // Backend will check for banned words and reject if found

  async function handleSave(e) {
    e.preventDefault();
    setError("");
    setSuccess("");

    // ✅ Content moderation is handled by backend
    // Backend will check for banned words and notify admins

    setSaving(true);

    // 2. Prepare Data (Backend expects 'linkedin', 'github', 'bio')
    const body = {
      bio,
      linkedin: linkedin.trim(),
      github: github.trim(),
    };

    const res = await apiFetch(
      "/api/profile",
      {
        method: "PUT",
        body: JSON.stringify(body),
      },
      { auth: true }
    );

    setSaving(false);

    if (!res.ok) {
      setError(res.error?.message || "Failed to save details");
    } else {
      setSuccess("Details saved successfully! Admin will review them.");
    }
  }

  async function handlePhotoUpload(e) {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setError("Please select a valid image (JPEG/PNG)");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    setSaving(true);
    const res = await apiFetch(
      "/api/profile/photo",
      {
        method: "POST",
        body: formData,
      },
      { auth: true }
    );
    setSaving(false);

    if (res.ok) {
      // Update photo immediately in the UI
      setProfilePhoto(res.data.url || URL.createObjectURL(file));
      setSuccess("Photo uploaded successfully!");
    } else {
      setError(res.error?.message || "Failed to upload photo");
    }
  }

  if (loading) {
    return (
      <div
        style={{
          height: "100vh",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          background: "#f0f2f5",
          color: "#333",
          fontSize: "1.2rem",
        }}
      >
        Loading your profile...
      </div>
    );
  }

  return (
    // Use flexbox for vertical and horizontal centering
    <div
      className={styles.page}
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        fontFamily: "sans-serif",
      }}
    >
      <img
        className={styles.bg}
        src={bg}
        alt="bg"
        style={{ objectFit: "cover" }}
      />
      <div className={styles.overlay} />

      {/* Main card */}
      <main
        className={styles.card}
        style={{
          maxWidth: "500px",
          width: "90%",
          textAlign: "center",
          padding: "30px",
          margin: "20px",
          zIndex: 2,
          position: "relative",
        }}
      >
        <img src={logo} alt="PSU" style={{ height: 60, marginBottom: 20 }} />

        <h1
          className={styles.title}
          style={{ color: "#d48806", fontSize: "1.8rem" }}
        >
          Account Pending
        </h1>
        <p className={styles.subtitle} style={{ marginBottom: "20px" }}>
          Your account is waiting for admin approval. <br />
          Complete your profile to speed up the process.
        </p>

        {/* ----- Profile Photo Upload ----- */}
        <div style={{ marginBottom: 25 }}>
          <div
            style={{
              position: "relative",
              width: 110,
              height: 110,
              margin: "0 auto",
            }}
          >
            <img
              src={profilePhoto || "https://via.placeholder.com/150"}
              alt="Profile"
              style={{
                width: "100%",
                height: "100%",
                borderRadius: "50%",
                objectFit: "cover",
                border: "4px solid #f0f2f5",
                boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
              }}
              onError={(e) => (e.currentTarget.style.opacity = "0.6")}
            />
            <label
              style={{
                position: "absolute",
                bottom: 5,
                right: 5,
                background: "#1890ff",
                width: 32,
                height: 32,
                borderRadius: "50%",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                cursor: "pointer",
                color: "white",
                boxShadow: "0 2px 5px rgba(0,0,0,0.2)",
              }}
              title="Change Photo"
            >
              📷
              <input
                type="file"
                accept="image/png, image/jpeg"
                onChange={handlePhotoUpload}
                style={{ display: "none" }}
              />
            </label>
          </div>
          <div style={{ fontSize: 13, color: "#888", marginTop: 8 }}>
            Click camera icon to upload
          </div>
        </div>

        {/* ----- Details Form ----- */}
        <form
          onSubmit={handleSave}
          style={{
            textAlign: "left",
            display: "flex",
            flexDirection: "column",
            gap: "15px",
          }}
        >
          <label className={styles.label}>
            Short Bio
            <textarea
              className={styles.input}
              rows={3}
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="Tell us about yourself..."
              style={{ resize: "vertical" }}
            />
          </label>

          <label className={styles.label}>
            LinkedIn URL
            <input
              className={styles.input}
              value={linkedin}
              onChange={(e) => setLinkedin(e.target.value)}
              placeholder="https://linkedin.com/in/..."
              type="url"
            />
          </label>

          <label className={styles.label}>
            GitHub URL
            <input
              className={styles.input}
              value={github}
              onChange={(e) => setGithub(e.target.value)}
              placeholder="https://github.com/..."
              type="url"
            />
          </label>

          {error && (
            <div
              style={{
                color: "#ff4d4f",
                background: "#fff1f0",
                padding: "10px",
                borderRadius: "6px",
                fontSize: "0.9rem",
                border: "1px solid #ffccc7",
              }}
            >
              {error}
            </div>
          )}

          {success && (
            <div
              style={{
                color: "#389e0d",
                background: "#f6ffed",
                padding: "10px",
                borderRadius: "6px",
                fontSize: "0.9rem",
                border: "1px solid #b7eb8f",
              }}
            >
              {success}
            </div>
          )}

          <button
            type="submit"
            className={styles.primaryBtn}
            disabled={saving}
            style={{ marginTop: "10px" }}
          >
            {saving ? "Saving..." : "Save Details"}
          </button>
        </form>

        <div
          style={{ marginTop: 25, borderTop: "1px solid #eee", paddingTop: 15 }}
        >
          <button
            onClick={handleLogout}
            style={{
              background: "none",
              border: "none",
              color: "#666",
              cursor: "pointer",
              textDecoration: "underline",
              fontSize: "0.9rem",
            }}
          >
            Logout & Check Later
          </button>
        </div>
      </main>
    </div>
  );
}
