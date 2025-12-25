/**
 * @fileoverview PublicProfilePage displays a user's public profile with visibility controls.
 * @module features/public/PublicProfilePage
 */

import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getPublicProfile } from "./public.api";
import bgImage from "../../assets/login-bg.jpg";
import { colors } from "../../core/theme";

/**
 * PublicProfilePage - Displays user profile based on visibility settings (PUBLIC/STUDENTS_ONLY/PRIVATE)
 * @component
 * @returns {JSX.Element} Public profile page with glass design
 */
export default function PublicProfilePage() {
  const { id } = useParams();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadProfile();
  }, [id]);

  async function loadProfile() {
    try {
      setLoading(true);
      const res = await getPublicProfile(id);
      setProfile(res.data || res);
    } catch (err) {
      setError("Profile not found or restricted.");
    } finally {
      setLoading(false);
    }
  }

  // --- Loading State (Glass Style) ---
  if (loading) return (
    <div className="min-h-screen flex items-center justify-center text-center" style={{
      backgroundImage: `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.8)), url(${bgImage})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      backgroundAttachment: "fixed"
    }}>
      <div className="text-white/70 text-lg">Loading Profile...</div>
    </div>
  );
  
  // --- Error/Private/Students Only State (Glass Style) ---
  // Backend handles visibility: PUBLIC (everyone), STUDENTS_ONLY (authenticated only), PRIVATE (owner/admin only)
  // If we get an error, it means access was denied
  if (error || (profile && (String(profile.visibility).toUpperCase() === "PRIVATE" || String(profile.visibility).toUpperCase() === "STUDENTS_ONLY"))) {
    const visibility = profile ? String(profile.visibility).toUpperCase() : null;
    const isStudentsOnly = visibility === "STUDENTS_ONLY";
    
    return (
      <div className="min-h-screen flex items-center justify-center text-center px-5" style={{
        backgroundImage: `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.8)), url(${bgImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed"
      }}>
        <div className="glass-card max-w-md p-10 text-center">
          <div className="text-4xl mb-4">🔒</div>
          <h3 className="text-white text-2xl font-bold m-0">
            {error || (isStudentsOnly ? "This profile is for students only." : "This profile is private.")}
          </h3>
          <p className="text-white/60 mt-2.5">
            {isStudentsOnly 
              ? "Please sign in to view this student's profile." 
              : "You cannot view this student's information."}
          </p>
          <Link to="/" className="inline-block mt-6 no-underline text-[#4de1ff] text-sm font-bold bg-[#00c6ff]/10 px-5 py-2.5 rounded-full hover:bg-[#00c6ff]/20 transition-all">Back to Directory</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center text-center px-5 py-20" style={{
      backgroundImage: `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.8)), url(${bgImage})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      backgroundAttachment: "fixed"
    }}>
      {/* Container limits width */}
      <div className="max-w-4xl w-full">
        
        {/* === MAIN GLASS CARD === */}
        <div className="glass-card p-0 overflow-hidden">
          
          {/* --- Header Section (Gradient Overlay) --- */}
          <div className="bg-gradient-to-b from-[#00c6ff]/10 to-transparent py-12 px-10 flex flex-wrap gap-8 items-center border-b border-white/10">
            {/* Profile Image with Glow */}
            <div className="relative">
              <div className="absolute -inset-1.5 bg-gradient-to-br from-[#00c6ff] to-[#0072ff] rounded-full opacity-60 blur-[15px]"></div>
              <img 
                src={profile.profilePhoto || profile.profilePhotoUrl || "https://via.placeholder.com/160"} 
                alt="Profile"
                className="w-36 h-36 rounded-full border-3 border-white/90 object-cover relative z-10" 
              />
            </div>
            
            <div className="flex-1 min-w-[250px] text-left">
              <h1 className="m-0 mb-2 text-white text-4xl font-bold" style={{ textShadow: "0 2px 4px rgba(0,0,0,0.5)" }}>
                {profile.firstName} {profile.lastName}
              </h1>
              
              <div className="flex flex-wrap gap-2.5 items-center text-white/80">
                <span className="bg-[#00c6ff]/15 border border-[#00c6ff]/30 px-3 py-1 rounded-full text-[#4de1ff] font-bold text-sm">
                  {profile.faculty}
                </span>
                <span>•</span>
                <span className="text-base">{profile.department}</span>
              </div>
            </div>

            {/* Social Actions */}
            <div className="flex gap-2.5">
              {profile.linkedin && (
                 <a href={profile.linkedin} target="_blank" rel="noreferrer" className="inline-flex items-center justify-center px-5 py-2 bg-[#0077b5] text-white no-underline rounded-xl text-sm font-semibold shadow-lg hover:opacity-90 transition-opacity">LinkedIn</a>
              )}
              {profile.github && (
                 <a href={profile.github} target="_blank" rel="noreferrer" className="inline-flex items-center justify-center px-5 py-2 bg-white/15 text-white no-underline rounded-xl text-sm font-semibold shadow-lg hover:bg-white/25 transition-all">GitHub</a>
              )}
            </div>
          </div>

          {/* --- Body Content --- */}
          <div className="grid grid-cols-[repeat(auto-fit,minmax(300px,1fr))]">
            
            {/* Left: Bio & Skills */}
            <div className="p-10 border-r border-white/10">
              <SectionTitle icon="📝" title="Biography" />
              <p className="leading-relaxed text-white/80 text-lg whitespace-pre-wrap">
                {profile.bio || "This student hasn't written a bio yet."}
              </p>

              <div className="mt-10">
                <SectionTitle icon="💡" title="Skills & Interests" />
                <div className="flex flex-wrap gap-2.5 mt-4">
                  {profile.interests ? (
                    profile.interests.split(/[,،\n]+/).map((tag, i) => (
                      tag.trim() && (
                        <span key={i} className="bg-white/10 px-4 py-2 rounded-full text-sm text-white border border-white/20">
                          {tag.trim()}
                        </span>
                      )
                    ))
                  ) : (
                     <span className="text-white/50">No skills listed.</span>
                  )}
                </div>
              </div>
            </div>

            {/* Right: Info Sidebar */}
            <div className="p-10 bg-black/10">
              <SectionTitle icon="ℹ️" title="Information" />
              
              <InfoItem label="Academic Year" value={`Year ${profile.year}`} />
              <InfoItem label="Email" value={profile.email} />
              
              {profile.phone && (
                 <InfoItem label="Phone" value={profile.phone} />
              )}
              
              <div className="mt-10 pt-5 border-t border-white/10">
                <Link to="/" className="inline-block no-underline text-[#4de1ff] text-sm font-bold bg-[#00c6ff]/10 px-5 py-2.5 rounded-full hover:bg-[#00c6ff]/20 transition-all">
                   ← Back to Directory
                </Link>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}

/**
 * SectionTitle - Displays a section header with icon
 * @param {object} props - Component props
 * @param {string} props.icon - Emoji icon
 * @param {string} props.title - Section title text
 * @returns {JSX.Element}
 */
function SectionTitle({ icon, title }) {
  return (
    <h3 className="text-white m-0 mb-5 flex items-center gap-2.5 text-xl border-b border-white/10 pb-2.5">
      <span>{icon}</span> {title}
    </h3>
  );
}

/**
 * InfoItem - Displays a labeled info field
 * @param {object} props - Component props
 * @param {string} props.label - Field label
 * @param {string} props.value - Field value
 * @returns {JSX.Element}
 */
function InfoItem({ label, value }) {
  return (
    <div className="mb-6">
      <div className="text-xs text-white/50 uppercase tracking-wider mb-1.5">{label}</div>
      <div className="text-base font-medium text-white break-all">{value || "N/A"}</div>
    </div>
  );
}