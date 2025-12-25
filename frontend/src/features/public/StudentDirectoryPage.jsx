/**
 * Student Directory page - Public listing of approved students.
 * Displays searchable and filterable directory of students who have set their profiles to public.
 * 
 * @module features/public/StudentDirectoryPage
 * @component
 * 
 * Features:
 * - Search by student name or faculty
 * - Filter by faculty
 * - Glass morphism card design
 * - Responsive grid layout
 * - Direct links to student profiles
 * - Loading and error states
 * 
 * @example
 * <Route path="/directory" element={<StudentDirectoryPage />} />
 * 
 * @returns {JSX.Element} Student directory page with search and filters
 */

import React, { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import { getPublicUsers } from "./public.api";
import bgImage from "../../assets/login-bg.jpg";
import { pageBackground } from "../../core/theme";

/**
 * StudentDirectoryPage - Public student directory with search and filters.
 * 
 * @component
 */
export default function StudentDirectoryPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  
  const [search, setSearch] = useState("");
  const [selectedFaculty, setSelectedFaculty] = useState("ALL");

  useEffect(() => {
    loadUsers();
  }, []);

  /**
   * Loads public student profiles from API.
   * 
   * @async
   */
  async function loadUsers() {
    try {
      setLoading(true);
      setError("");
      const res = await getPublicUsers();
      const data = Array.isArray(res) ? res : (res.data || []);
      setUsers(data);
    } catch (err) {
      console.error(err);
      setError("Failed to load directory."); 
    } finally {
      setLoading(false);
    }
  }

  /**
   * Extracts unique faculty names from loaded users.
   * Memoized to avoid recalculation on every render.
   */
  const faculties = useMemo(() => {
    const s = new Set(users.map(u => u.faculty).filter(Boolean));
    return ["ALL", ...Array.from(s)];
  }, [users]);

  /**
   * Filters users based on search term and selected faculty.
   */
  const filtered = users.filter(u => {
    const term = search.toLowerCase();
    const fullName = `${u.firstName} ${u.lastName}`.toLowerCase();
    const matchesSearch = fullName.includes(term) || (u.faculty && u.faculty.toLowerCase().includes(term));
    const matchesFaculty = selectedFaculty === "ALL" || u.faculty === selectedFaculty;
    return matchesSearch && matchesFaculty;
  });

  return (
    <div className="min-h-screen pb-16 pt-36 font-sans"
      style={{
        backgroundImage: `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.8)), url(${bgImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
      }}>
      
      {/* Hero Header */}
      <div className="text-center mb-16 px-5">
        <h1 className="text-white text-5xl font-extrabold mb-4 tracking-wide"
          style={{ textShadow: "0 4px 15px rgba(0,0,0,0.5)" }}>
          Student Directory
        </h1>
        <p className="text-white/85 text-xl max-w-2xl mx-auto"
          style={{ textShadow: "0 2px 4px rgba(0,0,0,0.5)" }}>
          Discover our talented community across all faculties.
        </p>

        {/* Search Toolbar */}
        <div className="max-w-3xl mx-auto mt-10 flex gap-4 flex-wrap glass-strong p-5 rounded-2xl">
          <div className="flex-1 min-w-[220px] relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-lg opacity-60">🔍</span>
            <input 
              type="text" 
              placeholder="Search by name..." 
              value={search}
              onChange={e => setSearch(e.target.value)}
              className="w-full py-3 pl-12 pr-3 rounded-xl border-none text-base outline-none bg-white/90 h-12"
            />
          </div>
          
          <select 
            value={selectedFaculty}
            onChange={e => setSelectedFaculty(e.target.value)}
            className="px-5 rounded-xl border-none bg-white/90 text-base outline-none cursor-pointer min-w-[180px] h-12 font-semibold text-gray-800"
          >
            {faculties.map(f => <option key={f} value={f}>{f === "ALL" ? "All Faculties" : f}</option>)}
          </select>
        </div>
      </div>

      {/* Grid Content */}
      <div className="max-w-7xl mx-auto px-5">
        
        {loading && <div className="text-center py-10 text-white/70 text-lg">Loading students...</div>}
        
        {!loading && !error && (
          <div className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-8">
            {filtered.length > 0 ? (
              filtered.map(user => (
                <div key={user.id} className="glass-card flex flex-col overflow-hidden transition-all duration-300 hover:-translate-y-2.5 hover:bg-white/12 hover:border-white/30">
                  {/* Card Body */}
                  <div className="p-8 flex flex-col items-center flex-1">
                    {/* Image with Glow */}
                    <div className="relative mb-5">
                      <div className="absolute -inset-1.5 bg-gradient-to-br from-[#00c6ff] to-[#0072ff] rounded-full z-0 opacity-70 blur-[10px]"></div>
                      <img 
                        src={user.profilePhotoUrl || user.profilePhoto || "https://via.placeholder.com/120"} 
                        alt={user.firstName}
                        className="w-28 h-28 rounded-full object-cover relative z-10 border-3 border-white/90"
                        onError={(e) => e.target.src = "https://via.placeholder.com/120"}
                      />
                    </div>
                    
                    <h3 className="my-1.5 text-2xl font-bold text-center tracking-wide">
                      {user.firstName} {user.lastName}
                    </h3>
                    
                    {/* Faculty Badge */}
                    <div className="text-xs text-white bg-gradient-to-r from-[#00c6ff] to-[#0072ff] px-4 py-1.5 rounded-full mb-4 mt-2.5 font-bold uppercase tracking-widest shadow-lg shadow-blue-500/40">
                      {user.faculty}
                    </div>
                    
                    <div className="text-sm text-white/70 text-center font-medium">
                      {user.department} • Year {user.year}
                    </div>
                  </div>

                  {/* Card Footer Button */}
                  <Link 
                    to={`/profile/${user.id}`}
                    className="block py-4.5 text-center bg-black/20 border-t border-white/10 no-underline text-white font-semibold tracking-widest text-xs uppercase transition-all duration-200 hover:bg-white/10 hover:text-[#00c6ff]"
                  >
                    View Profile
                  </Link>
                </div>
              ))
            ) : (
              <div className="col-span-full text-center py-15 text-white/60 bg-black/30 rounded-2xl">
                <div className="text-4xl mb-4 opacity-80">🕵️‍♂️</div>
                No students found matching your search.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}