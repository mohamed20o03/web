import React, { useEffect, useState } from "react";
import { apiFetch } from "../../core/api/http";
import { useNavigate } from "react-router-dom";
import { getFaculties, getDepartments } from "../public/public.api";

// ✅ Banned words are now managed in the backend
// Content moderation is handled server-side

export default function StudentProfilePage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [profile, setProfile] = useState(null);

  // States for Editing
  const [isEditing, setIsEditing] = useState(false);
  const [editBio, setEditBio] = useState("");
  const [editPhone, setEditPhone] = useState("");
  const [editLinkedin, setEditLinkedin] = useState("");
  const [editGithub, setEditGithub] = useState("");
  const [editInterests, setEditInterests] = useState("");
  const [editVisibility, setEditVisibility] = useState("PUBLIC");

  // Add state for national ID and scan
  const [editNationalId, setEditNationalId] = useState("");
  const [editNationalIdScan, setEditNationalIdScan] = useState(null);
  const [nationalIdScanUrl, setNationalIdScanUrl] = useState("");
  const [uploadingIdScan, setUploadingIdScan] = useState(false);

  const [uploading, setUploading] = useState(false);

  // Add state for user fields
  const [editFirstName, setEditFirstName] = useState("");
  const [editLastName, setEditLastName] = useState("");
  const [editFacultyId, setEditFacultyId] = useState("");
  const [editDepartmentId, setEditDepartmentId] = useState("");
  const [editYear, setEditYear] = useState(1);
  const [faculties, setFaculties] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingData, setLoadingData] = useState(true);

  useEffect(() => {
    loadProfile();
    // Load faculties on mount
    getFaculties().then((res) => {
      setFaculties(res || []);
      setLoadingData(false);
    });
  }, []);

  // Only redirect to /status if PENDING
  useEffect(() => {
    if (profile) {
      const status = String(profile.status || "").toUpperCase();
      if (status === "PENDING") {
        navigate("/status");
      }
    }
  }, [profile, navigate]);

  // Load departments when faculty changes
  useEffect(() => {
    if (editFacultyId) {
      getDepartments(editFacultyId).then((res) => {
        setDepartments(res || []);
      });
    }
  }, [editFacultyId]);

  async function loadProfile() {
    setLoading(true);
    setErr("");
    const res = await apiFetch(
      "/api/profile",
      { method: "GET" },
      { auth: true }
    );
    setLoading(false);

    if (!res.ok) {
      setErr(res.error?.message || "Failed to load profile");
      return;
    }
    const data = res.data;
    setProfile(data);

    setEditBio(data.bio || "");
    setEditPhone(data.phone || "");
    setEditLinkedin(data.linkedin || "");
    setEditGithub(data.github || "");
    setEditInterests(data.interests || "");
    // Convert value to uppercase to avoid dropdown issues
    setEditVisibility(String(data.visibility || "PUBLIC").toUpperCase());

    setEditFirstName(data.firstName || "");
    setEditLastName(data.lastName || "");
    setEditFacultyId(data.facultyId || "");
    setEditDepartmentId(data.departmentId || "");
    setEditYear(data.year || 1);
    setEditNationalId(data.nationalId || "");

    // In loadProfile, set the scan URL
    setNationalIdScanUrl(data.nationalIdScan || "");
  }

  // ✅ Content moderation is now handled by the backend
  // The backend will reject updates containing banned words and notify admins

  async function handlePhotoUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.type.startsWith("image/")) {
      alert("Images only");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    setUploading(true);
    const res = await apiFetch(
      "/api/profile/photo",
      { method: "POST", body: formData },
      { auth: true }
    );
    setUploading(false);

    if (!res.ok) {
      alert(res.error?.message || "Upload failed");
      return;
    }
    loadProfile();
  }

  // --- National ID Scan Upload Handler ---
  async function handleNationalIdScanUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.type.startsWith("image/")) {
      alert("Images only");
      return;
    }
    const formData = new FormData();
    formData.append("file", file);
    setUploadingIdScan(true);
    const res = await apiFetch(
      "/api/profile/national-id-scan",
      { method: "POST", body: formData },
      { auth: true }
    );
    setUploadingIdScan(false);
    if (!res.ok) {
      alert(res.error?.message || "Upload failed");
      return;
    }
    // Update scan URL in state and profile
    setNationalIdScanUrl(res.data.scanUrl);
    setEditNationalIdScan(null);
    loadProfile();
  }

  async function handleUpdate(e) {
    e.preventDefault();
    // ✅ Content moderation is handled by backend
    // Backend will check for banned words and reject if found

    const body = {
      firstName: editFirstName,
      lastName: editLastName,
      facultyId: editFacultyId,
      departmentId: editDepartmentId,
      year: editYear,
      bio: editBio,
      phone: editPhone,
      linkedin: editLinkedin,
      github: editGithub,
      interests: editInterests,
      visibility: editVisibility,
      nationalId: editNationalId,
      nationalIdScan: nationalIdScanUrl || "",
    };

    const res = await apiFetch(
      "/api/profile",
      { method: "PUT", body: JSON.stringify(body) },
      { auth: true }
    );
    if (!res.ok) {
      alert(res.error?.message || "Update failed");
      return;
    }

    setIsEditing(false);
    loadProfile();
    // alert("✅ Profile updated successfully!"); // Optional toast
  }

  // --- Helpers for Display ---

  // ✅ Helper functions for visibility states
  const getVisibilityState = () => {
    if (!profile) return null;
    return String(profile.visibility).toUpperCase();
  };

  const isPublic = getVisibilityState() === "PUBLIC";
  const isStudentsOnly = getVisibilityState() === "STUDENTS_ONLY";
  const isPrivate = getVisibilityState() === "PRIVATE";

  if (loading && !profile)
    return (
      <div style={{ textAlign: "center", paddingTop: 150, color: "#94a3b8" }}>
        Loading Profile...
      </div>
    );
  if (err)
    return (
      <div style={{ textAlign: "center", paddingTop: 150, color: "#f87171" }}>
        {err}
      </div>
    );

  // === Show rejection status and reason if rejected ===
  const isRejected =
    profile && String(profile.status || "").toUpperCase() === "REJECTED";
  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#0f172a",
        backgroundImage: "radial-gradient(at 50% 0%, #1e293b 0%, #0f172a 100%)",
        color: "white",
        fontFamily: "'Segoe UI', sans-serif",
        paddingTop: "120px",
        paddingBottom: "60px",
        paddingLeft: "20px",
        paddingRight: "20px",
      }}
    >
      <div style={{ maxWidth: 900, margin: "0 auto" }}>
        {/* === Show rejection alert if rejected === */}
        {isRejected && (
          <div
            style={{
              background: "rgba(239, 68, 68, 0.15)",
              border: "1px solid #ef4444",
              color: "#ef4444",
              borderRadius: 12,
              padding: "18px 24px",
              marginBottom: 30,
              fontSize: 16,
              fontWeight: 500,
              display: "flex",
              flexDirection: "column",
              gap: 6,
            }}
          >
            <span style={{ fontWeight: 700, fontSize: 18 }}>
              Your application was rejected
            </span>
            {profile.rejectionReason && (
              <span style={{ color: "#f87171", fontSize: 15 }}>
                Reason: {profile.rejectionReason}
              </span>
            )}
            <span style={{ color: "#fca5a5", fontSize: 14 }}>
              You can update your data and resubmit for review.
            </span>
          </div>
        )}
        {/* === Glass Card === */}
        <div
          style={{
            background: "rgba(255, 255, 255, 0.03)",
            backdropFilter: "blur(20px)",
            WebkitBackdropFilter: "blur(20px)",
            border: "1px solid rgba(255, 255, 255, 0.08)",
            borderRadius: "24px",
            padding: "40px",
            boxShadow: "0 8px 32px rgba(0, 0, 0, 0.3)",
          }}
        >
          {/* Header Area */}
          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: 30,
              alignItems: "center",
              marginBottom: 40,
              borderBottom: "1px solid rgba(255,255,255,0.08)",
              paddingBottom: 30,
            }}
          >
            {/* Photo with Glow */}
            <div style={{ position: "relative" }}>
              <div
                style={{
                  position: "absolute",
                  top: -8,
                  left: -8,
                  right: -8,
                  bottom: -8,
                  background: "linear-gradient(45deg, #00c6ff, #0072ff)",
                  borderRadius: "50%",
                  opacity: 0.5,
                  filter: "blur(15px)",
                  zIndex: 0,
                }}
              ></div>

              <img
                src={profile.profilePhoto || "https://via.placeholder.com/150"}
                alt="Profile"
                style={{
                  width: 130,
                  height: 130,
                  borderRadius: "50%",
                  objectFit: "cover",
                  border: "3px solid rgba(255,255,255,0.9)",
                  position: "relative",
                  zIndex: 1,
                  opacity: uploading ? 0.5 : 1,
                }}
                onError={(e) =>
                  (e.target.src = "https://via.placeholder.com/150")
                }
              />

              <label
                style={{
                  position: "absolute",
                  bottom: 5,
                  right: 5,
                  zIndex: 2,
                  background: "#3b82f6",
                  width: 36,
                  height: 36,
                  borderRadius: "50%",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  cursor: "pointer",
                  border: "3px solid #0f172a",
                  boxShadow: "0 4px 10px rgba(0,0,0,0.3)",
                }}
                title="Change Photo"
              >
                <span style={{ fontSize: 16 }}>📷</span>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoUpload}
                  style={{ display: "none" }}
                />
              </label>
            </div>

            <div style={{ flex: 1 }}>
              <h1
                style={{
                  margin: "0 0 10px",
                  fontSize: "2.2rem",
                  fontWeight: "700",
                  letterSpacing: "-0.5px",
                }}
              >
                {profile.firstName} {profile.lastName}
              </h1>
              <div
                style={{
                  color: "#94a3b8",
                  fontSize: "1.1rem",
                  display: "flex",
                  flexWrap: "wrap",
                  gap: 15,
                  alignItems: "center",
                }}
              >
                <span>{profile.email}</span>
                <span style={{ color: "rgba(255,255,255,0.2)" }}>|</span>
                <span style={{ color: "#38bdf8", fontWeight: "500" }}>
                  {profile.faculty}
                </span>
              </div>

              {/* ✅ Visibility Badge (Updated Logic) */}
              <div style={{ marginTop: 15 }}>
                <span
                  style={{
                    padding: "6px 14px",
                    borderRadius: 20,
                    background: isPublic
                      ? "rgba(34, 197, 94, 0.15)"
                      : isStudentsOnly
                      ? "rgba(251, 191, 36, 0.15)"
                      : "rgba(248, 113, 113, 0.15)",
                    color: isPublic
                      ? "#4ade80"
                      : isStudentsOnly
                      ? "#fbbf24"
                      : "#f87171",
                    border: isPublic
                      ? "1px solid rgba(34, 197, 94, 0.3)"
                      : isStudentsOnly
                      ? "1px solid rgba(251, 191, 36, 0.3)"
                      : "1px solid rgba(248, 113, 113, 0.3)",
                    fontSize: 12,
                    fontWeight: "bold",
                    letterSpacing: 1,
                  }}
                >
                  {isPublic
                    ? "● PUBLIC"
                    : isStudentsOnly
                    ? "● STUDENTS ONLY"
                    : "● PRIVATE"}
                </span>
              </div>
            </div>

            {/* Edit Toggle Button */}
            <div>
              <button
                onClick={() => setIsEditing(!isEditing)}
                style={{
                  background: isEditing
                    ? "rgba(255,255,255,0.1)"
                    : "linear-gradient(90deg, #3b82f6 0%, #2563eb 100%)",
                  color: "white",
                  border: isEditing
                    ? "1px solid rgba(255,255,255,0.2)"
                    : "none",
                  padding: "10px 24px",
                  borderRadius: "12px",
                  cursor: "pointer",
                  fontWeight: "600",
                  fontSize: "14px",
                  transition: "all 0.2s",
                  boxShadow: isEditing
                    ? "none"
                    : "0 4px 20px rgba(37, 99, 235, 0.4)",
                }}
              >
                {isEditing ? "Cancel" : "Edit Profile ✎"}
              </button>
            </div>
          </div>

          {/* === Content Area === */}
          {isEditing ? (
            /* --- EDIT FORM (Glass Style) --- */
            <form
              onSubmit={handleUpdate}
              style={{
                display: "flex",
                flexDirection: "column",
                gap: 25,
                animation: "fadeIn 0.3s ease",
              }}
            >
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: 20,
                }}
              >
                <FormInput
                  label="First Name"
                  value={editFirstName}
                  onChange={(e) => setEditFirstName(e.target.value)}
                  placeholder="First Name"
                />
                <FormInput
                  label="Last Name"
                  value={editLastName}
                  onChange={(e) => setEditLastName(e.target.value)}
                  placeholder="Last Name"
                />
              </div>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr 1fr 1fr",
                  gap: 20,
                }}
              >
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 8 }}
                >
                  <label
                    style={{
                      fontSize: 13,
                      color: "#94a3b8",
                      fontWeight: "600",
                    }}
                  >
                    Faculty
                  </label>
                  <select
                    value={editFacultyId}
                    onChange={(e) => setEditFacultyId(e.target.value)}
                    style={inputStyle}
                  >
                    <option value="">Select Faculty</option>
                    {faculties.map((f) => (
                      <option key={f.id} value={f.id}>
                        {f.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 8 }}
                >
                  <label
                    style={{
                      fontSize: 13,
                      color: "#94a3b8",
                      fontWeight: "600",
                    }}
                  >
                    Department
                  </label>
                  <select
                    value={editDepartmentId}
                    onChange={(e) => setEditDepartmentId(e.target.value)}
                    style={inputStyle}
                  >
                    <option value="">Select Department</option>
                    {departments.map((d) => (
                      <option key={d.id} value={d.id}>
                        {d.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 8 }}
                >
                  <label
                    style={{
                      fontSize: 13,
                      color: "#94a3b8",
                      fontWeight: "600",
                    }}
                  >
                    Year
                  </label>
                  <input
                    type="number"
                    min={1}
                    max={10}
                    value={editYear}
                    onChange={(e) => setEditYear(Number(e.target.value))}
                    style={inputStyle}
                  />
                </div>
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 8 }}
                >
                  <label
                    style={{
                      fontSize: 13,
                      color: "#94a3b8",
                      fontWeight: "600",
                    }}
                  >
                    National ID
                  </label>
                  <input
                    type="text"
                    value={editNationalId}
                    onChange={(e) => setEditNationalId(e.target.value)}
                    style={inputStyle}
                    placeholder="Enter your 14-digit National ID"
                  />
                </div>
              </div>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: 20,
                }}
              >
                <FormInput
                  label="Phone Number"
                  value={editPhone}
                  onChange={(e) => setEditPhone(e.target.value)}
                  placeholder="+20 1xxxxxxxxx"
                />

                <div
                  style={{ display: "flex", flexDirection: "column", gap: 8 }}
                >
                  <label
                    style={{
                      fontSize: 13,
                      color: "#94a3b8",
                      fontWeight: "600",
                    }}
                  >
                    Profile Visibility
                  </label>
                  <div style={{ position: "relative" }}>
                    <select
                      value={editVisibility}
                      onChange={(e) => setEditVisibility(e.target.value)}
                      style={{
                        ...inputStyle,
                        appearance: "none",
                        cursor: "pointer",
                      }}
                    >
                      <option value="PUBLIC" style={{ color: "black" }}>
                        Public (Visible to everyone)
                      </option>
                      <option value="STUDENTS_ONLY" style={{ color: "black" }}>
                        Students Only (Visible to signed-in users)
                      </option>
                      <option value="PRIVATE" style={{ color: "black" }}>
                        Private (Only you and admins)
                      </option>
                    </select>
                    <div
                      style={{
                        position: "absolute",
                        right: 15,
                        top: "50%",
                        transform: "translateY(-50%)",
                        pointerEvents: "none",
                        fontSize: 12,
                      }}
                    >
                      ▼
                    </div>
                  </div>
                  <div style={{ fontSize: 12, color: "#64748b" }}>
                    * Determines who can see your profile.
                  </div>
                </div>
              </div>

              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <label
                  style={{ fontSize: 13, color: "#94a3b8", fontWeight: "600" }}
                >
                  Bio
                </label>
                <textarea
                  rows={4}
                  value={editBio}
                  onChange={(e) => setEditBio(e.target.value)}
                  style={{ ...inputStyle, resize: "vertical" }}
                  placeholder="Tell us about yourself..."
                />
              </div>

              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <label
                  style={{ fontSize: 13, color: "#94a3b8", fontWeight: "600" }}
                >
                  Skills & Interests
                </label>
                <textarea
                  rows={2}
                  value={editInterests}
                  onChange={(e) => setEditInterests(e.target.value)}
                  style={inputStyle}
                  placeholder="e.g. Java, React, Football, Reading (comma separated)"
                />
              </div>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: 20,
                }}
              >
                <FormInput
                  label="LinkedIn URL"
                  value={editLinkedin}
                  onChange={(e) => setEditLinkedin(e.target.value)}
                  placeholder="https://linkedin.com/in/..."
                />
                <FormInput
                  label="GitHub URL"
                  value={editGithub}
                  onChange={(e) => setEditGithub(e.target.value)}
                  placeholder="https://github.com/..."
                />
              </div>

              {/* National ID Scan Upload Section */}
              <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                <label
                  style={{ fontSize: 13, color: "#94a3b8", fontWeight: "600" }}
                >
                  National ID Scan
                </label>
                {nationalIdScanUrl && (
                  <img
                    src={
                      nationalIdScanUrl
                        ? `${nationalIdScanUrl}?t=${Date.now()}`
                        : ""
                    }
                    alt="National ID Scan"
                    style={{
                      width: 180,
                      borderRadius: 8,
                      marginBottom: 8,
                      border: "1px solid #334155",
                    }}
                  />
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleNationalIdScanUpload}
                  disabled={uploadingIdScan}
                  style={{ color: "white" }}
                />
                {uploadingIdScan && (
                  <span style={{ color: "#38bdf8", fontSize: 13 }}>
                    Uploading...
                  </span>
                )}
              </div>

              <div
                style={{
                  paddingTop: 20,
                  borderTop: "1px solid rgba(255,255,255,0.1)",
                  display: "flex",
                  justifyContent: "flex-end",
                  gap: 15,
                }}
              >
                <button type="submit" style={primaryBtnStyle}>
                  Save Changes
                </button>
              </div>
            </form>
          ) : (
            /* --- VIEW MODE --- */
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "2fr 1fr",
                gap: 50,
                animation: "fadeIn 0.3s ease",
              }}
            >
              {/* Left Column */}
              <div
                style={{
                  borderRight: "1px solid rgba(255,255,255,0.08)",
                  paddingRight: 40,
                }}
              >
                <SectionHeader title="Biography" icon="📝" />
                <p
                  style={{
                    lineHeight: 1.8,
                    color: "#cbd5e1",
                    whiteSpace: "pre-wrap",
                    fontSize: "1.05rem",
                  }}
                >
                  {profile.bio || "No biography added yet."}
                </p>

                <div style={{ marginTop: 40 }}>
                  <SectionHeader title="Skills & Interests" icon="💡" />
                  <div
                    style={{
                      display: "flex",
                      flexWrap: "wrap",
                      gap: 10,
                      marginTop: 15,
                    }}
                  >
                    {profile.interests ? (
                      profile.interests.split(/[,،\n]+/).map(
                        (tag, i) =>
                          tag.trim() && (
                            <span
                              key={i}
                              style={{
                                background: "rgba(56, 189, 248, 0.1)",
                                color: "#38bdf8",
                                padding: "6px 14px",
                                borderRadius: 20,
                                fontSize: 13,
                                border: "1px solid rgba(56, 189, 248, 0.2)",
                                fontWeight: "500",
                              }}
                            >
                              {tag.trim()}
                            </span>
                          )
                      )
                    ) : (
                      <span style={{ color: "#64748b" }}>
                        No skills listed.
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {/* Right Column */}
              <div>
                <SectionHeader title="Academic Info" icon="🎓" />
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 15,
                    marginBottom: 40,
                  }}
                >
                  <InfoRow label="Department" val={profile.department} />
                  <InfoRow label="Year" val={`Year ${profile.year}`} />
                  <InfoRow label="National ID" val={profile.nationalId} />
                </div>

                <SectionHeader title="Contact" icon="🔗" />
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 12 }}
                >
                  {profile.linkedin ? (
                    <a
                      href={profile.linkedin}
                      target="_blank"
                      rel="noreferrer"
                      style={socialLinkStyle("#0a66c2")}
                    >
                      LinkedIn Profile ↗
                    </a>
                  ) : (
                    <span style={noLinkStyle}>No LinkedIn</span>
                  )}

                  {profile.github ? (
                    <a
                      href={profile.github}
                      target="_blank"
                      rel="noreferrer"
                      style={socialLinkStyle("#ffffff")}
                    >
                      GitHub Profile ↗
                    </a>
                  ) : (
                    <span style={noLinkStyle}>No GitHub</span>
                  )}

                  {profile.phone ? (
                    <div
                      style={{
                        color: "#cbd5e1",
                        fontSize: 14,
                        display: "flex",
                        alignItems: "center",
                        gap: 8,
                      }}
                    >
                      <span>📞</span> {profile.phone}
                    </div>
                  ) : (
                    <span style={noLinkStyle}>No Phone</span>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <style>{`
        @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
      `}</style>
    </div>
  );
}

// === Sub-Components ===

function SectionHeader({ title, icon }) {
  return (
    <h3
      style={{
        margin: "0 0 15px",
        color: "white",
        fontSize: "1.2rem",
        display: "flex",
        alignItems: "center",
        gap: 10,
      }}
    >
      <span>{icon}</span> {title}
    </h3>
  );
}

function FormInput({ label, value, onChange, placeholder }) {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
      <label style={{ fontSize: 13, color: "#94a3b8", fontWeight: "600" }}>
        {label}
      </label>
      <input
        type="text"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        style={inputStyle}
      />
    </div>
  );
}

function InfoRow({ label, val }) {
  return (
    <div>
      <div
        style={{
          fontSize: 12,
          color: "#64748b",
          textTransform: "uppercase",
          marginBottom: 3,
          letterSpacing: 0.5,
        }}
      >
        {label}
      </div>
      <div style={{ fontSize: 15, fontWeight: "500", color: "#e2e8f0" }}>
        {val}
      </div>
    </div>
  );
}

// === Styles ===
const inputStyle = {
  width: "100%",
  padding: "12px 16px",
  background: "rgba(0, 0, 0, 0.3)", // Dark field background
  border: "1px solid rgba(255, 255, 255, 0.1)",
  borderRadius: "12px",
  color: "white",
  fontSize: "14px",
  outline: "none",
  transition: "border-color 0.2s",
};

const primaryBtnStyle = {
  background: "linear-gradient(90deg, #3b82f6 0%, #2563eb 100%)",
  color: "white",
  border: "none",
  padding: "12px 30px",
  borderRadius: "12px",
  fontWeight: "bold",
  cursor: "pointer",
  boxShadow: "0 4px 15px rgba(37, 99, 235, 0.4)",
  fontSize: "15px",
};

const secondaryBtnStyle = {
  background: "transparent",
  color: "#cbd5e1",
  border: "1px solid rgba(255,255,255,0.2)",
  padding: "12px 24px",
  borderRadius: "12px",
  cursor: "pointer",
  fontWeight: "600",
};

const socialLinkStyle = (color) => ({
  color: color,
  textDecoration: "none",
  fontSize: 14,
  fontWeight: "500",
  background: "rgba(255,255,255,0.05)",
  padding: "10px 15px",
  borderRadius: "10px",
  border: "1px solid rgba(255,255,255,0.05)",
  display: "inline-block",
  transition: "0.2s",
});

const noLinkStyle = {
  color: "#475569",
  fontSize: 13,
  fontStyle: "italic",
  padding: "5px 0",
};
