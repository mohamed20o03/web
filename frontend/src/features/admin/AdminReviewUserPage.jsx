//eslint-disable react-hooks/exhaustive-deps
import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import {
  getUserDetails,
  getStudentProfile,
  approveRejectUser,
  sendVerification,
  verifyEmailWithToken,
  changeUserRole,
} from "./admin.api";
// Import unified background image
import bgImage from "../../assets/login-bg.jpg";

export default function AdminReviewUserPage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [processing, setProcessing] = useState(false);

  const [rejectReason, setRejectReason] = useState("");
  const [showRejectInput, setShowRejectInput] = useState(false);

  // Manual Verify State
  const [showManualVerify, setShowManualVerify] = useState(false);
  const [manualToken, setManualToken] = useState("");

  useEffect(() => {
    if (userId && userId !== "undefined") {
      loadAllData();
    } else {
      setError("Invalid User ID");
      setLoading(false);
    }
  }, [userId]);

  async function loadAllData() {
    setLoading(true);
    try {
      const userRes = await getUserDetails(userId);
      setUser(userRes.data || userRes);

      try {
        const profileRes = await getStudentProfile(userId);
        setProfile(profileRes.data || profileRes);
      } catch (err) {
        console.log("No extra profile info (might be null)");
      }
    } catch (err) {
      setError(err.message || "Failed to load user");
    } finally {
      setLoading(false);
    }
  }

  async function handleSendEmail() {
    setProcessing(true);
    try {
      const res = await sendVerification(userId);
      const data = res.data || res || {};

      let msg = "Verification email sent!";
      // If backend testingMode=true, it returns the token
      if (data.token) {
        msg += `\n\n[TEST TOKEN]: ${data.token}\n\n(Copied to console as well)`;
        console.log("TEST TOKEN:", data.token);
        // Open manual verification input automatically
        setShowManualVerify(true);
        setManualToken(data.token); // Convenience: auto-fill the token in the input field
      }
      alert(msg);
    } catch (err) {
      alert(err.message || "Failed to send verification");
    } finally {
      setProcessing(false);
    }
  }

  async function handleManualVerify() {
    if (!manualToken.trim()) return alert("Please enter the token!");

    setProcessing(true);
    try {
      await verifyEmailWithToken(userId, manualToken);
      alert("✅ Email Verified Successfully!");
      setShowManualVerify(false);
      loadAllData(); // Refresh the page data
    } catch (err) {
      alert(err.message || "Verification Failed");
    } finally {
      setProcessing(false);
    }
  }

  async function handleApprove() {
    if (!window.confirm("Are you sure you want to APPROVE this student?"))
      return;
    setProcessing(true);
    try {
      await approveRejectUser({ userId: parseInt(userId), approved: true });
      alert("User Approved!");
      navigate("/admin/users");
    } catch (err) {
      alert(err.message);
      setProcessing(false);
    }
  }

  async function handleReject() {
    if (!rejectReason.trim())
      return alert("Please provide a rejection reason.");
    setProcessing(true);
    try {
      await approveRejectUser({
        userId: parseInt(userId),
        approved: false,
        rejectionReason: rejectReason,
      });
      alert("User Rejected.");
      navigate("/admin/users");
    } catch (err) {
      alert(err.message);
      setProcessing(false);
    }
  }

  async function handleChangeRole(newRole) {
    const roleName = newRole === "ADMIN" ? "admin" : "student";
    if (
      !window.confirm(
        `Are you sure you want to change this user's role to ${roleName.toUpperCase()}?`
      )
    )
      return;

    setProcessing(true);
    try {
      await changeUserRole(parseInt(userId), newRole);
      alert(`User role changed to ${roleName.toUpperCase()} successfully!`);
      loadAllData(); // Reload to show updated role
    } catch (err) {
      alert(err.message || "Failed to change role");
    } finally {
      setProcessing(false);
    }
  }

  if (loading) return <div style={centerMsgStyle}>Loading details...</div>;
  if (error)
    return <div style={{ ...centerMsgStyle, color: "#fca5a5" }}>{error}</div>;
  if (!user) return <div style={centerMsgStyle}>User not found</div>;

  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundImage: `linear-gradient(rgba(0,0,0,0.8), rgba(0,0,0,0.9)), url(${bgImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
        padding: "120px 20px 60px", // Padding top for Navbar
        fontFamily: "'Segoe UI', sans-serif",
        color: "white",
      }}
    >
      <div style={{ maxWidth: 1100, margin: "0 auto" }}>
        {/* Header & Back Button */}
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 20,
            marginBottom: 30,
          }}
        >
          <Link to="/admin/pending" style={backBtnStyle}>
            ← Back to List
          </Link>
          <h1 style={{ margin: 0, fontSize: 28 }}>Review Application</h1>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(450px, 1fr))",
            gap: 30,
          }}
        >
          {/* === LEFT COLUMN: Identity Verification === */}
          <div style={cardStyle}>
            <h3 style={cardTitleStyle}>Identity Match</h3>
            <p style={{ color: "#94a3b8", fontSize: 13, marginBottom: 20 }}>
              Compare the profile photo with the National ID scan.
            </p>

            <div
              style={{
                display: "flex",
                gap: 20,
                justifyContent: "space-around",
                flexWrap: "wrap",
              }}
            >
              {/* Profile Photo */}
              <div style={{ textAlign: "center" }}>
                <div style={imageLabelStyle}>Profile Photo</div>
                <img
                  src={user.profilePhotoUrl || user.profilePhoto}
                  alt="Profile"
                  style={compareImgStyle}
                />
              </div>

              {/* ID Scan */}
              <div style={{ textAlign: "center" }}>
                <div style={imageLabelStyle}>National ID Scan</div>
                <a
                  href={user.nationalIdScanUrl || user.nationalIdScan}
                  target="_blank"
                  rel="noreferrer"
                >
                  <img
                    src={user.nationalIdScanUrl || user.nationalIdScan}
                    alt="ID Scan"
                    style={{
                      ...compareImgStyle,
                      borderRadius: 8,
                      cursor: "zoom-in",
                    }}
                    title="Click to open full size"
                  />
                </a>
              </div>
            </div>

            <div
              style={{
                marginTop: 30,
                paddingTop: 20,
                borderTop: "1px solid rgba(255,255,255,0.1)",
              }}
            >
              <InfoRow
                label="Full Name"
                value={`${user.firstName} ${user.lastName}`}
              />
              <InfoRow label="National ID" value={user.nationalId} />
              <InfoRow label="Faculty" value={user.faculty} />
              <InfoRow label="Department" value={user.department} />
            </div>
          </div>

          {/* === RIGHT COLUMN: Email & Decision === */}
          <div style={{ display: "flex", flexDirection: "column", gap: 30 }}>
            {/* 1. Email Verification Status */}
            <div style={cardStyle}>
              <h3 style={cardTitleStyle}>Email Verification</h3>

              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 15,
                  marginBottom: 20,
                }}
              >
                <span style={{ fontSize: 16 }}>{user.email}</span>
                {user.emailVerified ? (
                  <span style={badgeSuccess}>Verified</span>
                ) : (
                  <span style={badgeWarning}>Unverified</span>
                )}
              </div>

              {!user.emailVerified && (
                <div
                  style={{
                    background: "rgba(0,0,0,0.2)",
                    padding: 15,
                    borderRadius: 10,
                  }}
                >
                  <p
                    style={{
                      margin: "0 0 10px",
                      fontSize: 13,
                      color: "#cbd5e1",
                    }}
                  >
                    User needs to verify email before approval.
                  </p>

                  <button
                    onClick={handleSendEmail}
                    disabled={processing}
                    style={actionBtnStyle("#0ea5e9")}
                  >
                    {processing
                      ? "Sending..."
                      : "✉️ Send Verification (Get Token)"}
                  </button>

                  <div style={{ marginTop: 15 }}>
                    {!showManualVerify ? (
                      <button
                        onClick={() => setShowManualVerify(true)}
                        style={{
                          background: "transparent",
                          border: "none",
                          color: "#94a3b8",
                          fontSize: 12,
                          cursor: "pointer",
                          textDecoration: "underline",
                        }}
                      >
                        Enter Token Manually
                      </button>
                    ) : (
                      <div style={{ display: "flex", gap: 10 }}>
                        <input
                          style={inputStyle}
                          placeholder="Paste Token here..."
                          value={manualToken}
                          onChange={(e) => setManualToken(e.target.value)}
                        />
                        <button
                          onClick={handleManualVerify}
                          disabled={processing}
                          style={actionBtnStyle("#22c55e")}
                        >
                          Verify
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* 2. Role Management */}
            <div style={cardStyle}>
              <h3 style={cardTitleStyle}>Role Management</h3>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 15,
                  marginBottom: 15,
                }}
              >
                <span style={{ fontSize: 14, color: "#cbd5e1" }}>
                  Current Role:
                </span>
                {String(user.role || "").toUpperCase() === "ADMIN" ||
                String(user.role || "").toUpperCase() === "ROLE_ADMIN" ? (
                  <span
                    style={{
                      display: "inline-flex",
                      alignItems: "center",
                      gap: 6,
                      padding: "6px 14px",
                      borderRadius: "20px",
                      background: "rgba(234, 179, 8, 0.15)",
                      color: "#facc15",
                      border: "1px solid rgba(234, 179, 8, 0.3)",
                      fontSize: "13px",
                      fontWeight: "bold",
                    }}
                  >
                    👑 ADMIN
                  </span>
                ) : (
                  <span
                    style={{
                      padding: "6px 14px",
                      borderRadius: "20px",
                      background: "rgba(148, 163, 184, 0.15)",
                      color: "#cbd5e1",
                      border: "1px solid rgba(148, 163, 184, 0.2)",
                      fontSize: "13px",
                      fontWeight: "500",
                    }}
                  >
                    Student
                  </span>
                )}
              </div>

              <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                {String(user.role || "").toUpperCase() !== "ADMIN" &&
                String(user.role || "").toUpperCase() !== "ROLE_ADMIN" ? (
                  <button
                    onClick={() => handleChangeRole("ADMIN")}
                    disabled={processing}
                    style={actionBtnStyle("#facc15")}
                  >
                    👑 Make Admin
                  </button>
                ) : (
                  <button
                    onClick={() => handleChangeRole("STUDENT")}
                    disabled={processing}
                    style={{
                      ...actionBtnStyle("transparent"),
                      border: "1px solid #64748b",
                      color: "#94a3b8",
                    }}
                  >
                    Demote to Student
                  </button>
                )}
              </div>
            </div>

            {/* 3. Profile Preview (Bio) */}
            <div style={cardStyle}>
              <h3 style={cardTitleStyle}>Profile Content</h3>
              {profile ? (
                <div
                  style={{ fontSize: 14, color: "#cbd5e1", lineHeight: 1.6 }}
                >
                  <strong>Bio:</strong> {profile.bio || "No bio provided."}
                  <div style={{ marginTop: 10, display: "flex", gap: 10 }}>
                    {profile.linkedin && (
                      <a
                        href={profile.linkedin}
                        target="_blank"
                        rel="noreferrer"
                        style={{ color: "#38bdf8" }}
                      >
                        LinkedIn
                      </a>
                    )}
                    {profile.github && (
                      <a
                        href={profile.github}
                        target="_blank"
                        rel="noreferrer"
                        style={{ color: "#38bdf8" }}
                      >
                        GitHub
                      </a>
                    )}
                  </div>
                </div>
              ) : (
                <div style={{ color: "#64748b", fontStyle: "italic" }}>
                  No extra profile data.
                </div>
              )}
            </div>

            {/* 4. Final Decision */}
            {/* Only show approve/reject for pending users */}
            {String(user.status || "").toUpperCase() === "PENDING" && (
              <div
                style={{
                  ...cardStyle,
                  border: "1px solid rgba(255,255,255,0.3)",
                }}
              >
                <h3 style={cardTitleStyle}>Admin Decision</h3>
                {showRejectInput ? (
                  <div
                    style={{
                      display: "flex",
                      flexDirection: "column",
                      gap: 10,
                    }}
                  >
                    <textarea
                      value={rejectReason}
                      onChange={(e) => setRejectReason(e.target.value)}
                      placeholder="Reason for rejection..."
                      rows={3}
                      style={{ ...inputStyle, resize: "vertical" }}
                    />
                    <div style={{ display: "flex", gap: 10 }}>
                      <button
                        onClick={() => setShowRejectInput(false)}
                        style={actionBtnStyle("#64748b")}
                      >
                        Cancel
                      </button>
                      <button
                        onClick={handleReject}
                        disabled={processing}
                        style={actionBtnStyle("#ef4444")}
                      >
                        Confirm Reject
                      </button>
                    </div>
                  </div>
                ) : (
                  <div style={{ display: "flex", gap: 15 }}>
                    <button
                      onClick={() => setShowRejectInput(true)}
                      disabled={processing}
                      style={{
                        ...actionBtnStyle("transparent"),
                        border: "1px solid #ef4444",
                        color: "#ef4444",
                      }}
                    >
                      Reject Application
                    </button>
                    <button
                      onClick={handleApprove}
                      disabled={processing || !user.emailVerified}
                      style={{
                        ...actionBtnStyle("#22c55e"),
                        flex: 1,
                        opacity: !user.emailVerified ? 0.5 : 1,
                        cursor: !user.emailVerified ? "not-allowed" : "pointer",
                      }}
                      title={!user.emailVerified ? "Verify email first" : ""}
                    >
                      Approve Application
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

// === Styles ===
const centerMsgStyle = {
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  height: "100vh",
  color: "white",
  background: "#0f172a",
  fontSize: 18,
};

const backBtnStyle = {
  textDecoration: "none",
  color: "#94a3b8",
  fontWeight: "bold",
  background: "rgba(255,255,255,0.1)",
  padding: "8px 15px",
  borderRadius: 20,
  fontSize: 14,
};

const cardStyle = {
  background: "rgba(255, 255, 255, 0.05)",
  backdropFilter: "blur(12px)",
  border: "1px solid rgba(255, 255, 255, 0.1)",
  borderRadius: 16,
  padding: 25,
  boxShadow: "0 4px 20px rgba(0,0,0,0.2)",
};

const cardTitleStyle = {
  margin: "0 0 15px 0",
  fontSize: 18,
  borderBottom: "1px solid rgba(255,255,255,0.1)",
  paddingBottom: 10,
};

const compareImgStyle = {
  width: 150,
  height: 150,
  objectFit: "cover",
  borderRadius: "50%",
  border: "3px solid rgba(255,255,255,0.2)",
  background: "black",
};

const imageLabelStyle = {
  marginBottom: 8,
  fontSize: 12,
  color: "#94a3b8",
  textTransform: "uppercase",
  letterSpacing: 1,
};

const badgeSuccess = {
  background: "rgba(34, 197, 94, 0.2)",
  color: "#4ade80",
  padding: "4px 10px",
  borderRadius: 12,
  fontSize: 12,
  fontWeight: "bold",
  border: "1px solid rgba(34, 197, 94, 0.3)",
};

const badgeWarning = {
  background: "rgba(234, 179, 8, 0.2)",
  color: "#facc15",
  padding: "4px 10px",
  borderRadius: 12,
  fontSize: 12,
  fontWeight: "bold",
  border: "1px solid rgba(234, 179, 8, 0.3)",
};

const inputStyle = {
  width: "100%",
  padding: "10px",
  background: "rgba(0,0,0,0.3)",
  border: "1px solid rgba(255,255,255,0.1)",
  borderRadius: 8,
  color: "white",
  outline: "none",
};

const actionBtnStyle = (bg) => ({
  background: bg,
  color: "white",
  border: "none",
  padding: "10px 20px",
  borderRadius: 8,
  cursor: "pointer",
  fontWeight: "bold",
  transition: "0.2s",
});

function InfoRow({ label, value }) {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        marginBottom: 12,
        borderBottom: "1px dashed rgba(255,255,255,0.1)",
        paddingBottom: 8,
      }}
    >
      <span style={{ color: "#94a3b8", fontSize: 14 }}>{label}</span>
      <span style={{ fontWeight: 500 }}>{value}</span>
    </div>
  );
}
