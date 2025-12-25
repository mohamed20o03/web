/**
 * Academic reference data domain.
 * Provides faculty and department lookup data for user registration.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Models:</strong> Faculty, Department entities</li>
 *   <li><strong>Repositories:</strong> FacultyRepository, DepartmentRepository</li>
 *   <li><strong>DTOs:</strong> Faculty and department responses</li>
 * </ul>
 *
 * <p>Data structure:
 * <ul>
 *   <li><strong>Faculty:</strong> Top-level academic unit (e.g., Engineering, Medicine)</li>
 *   <li><strong>Department:</strong> Subdivision within faculty (e.g., Computer Science, Mechanical)</li>
 *   <li>Each department belongs to exactly one faculty</li>
 *   <li>Each faculty has multiple departments and specified number of academic years</li>
 * </ul>
 *
 * <p>Usage in registration:
 * <ol>
 *   <li>User selects faculty from available list</li>
 *   <li>Departments filtered to show only those belonging to selected faculty</li>
 *   <li>User selects department and academic year</li>
 *   <li>Year validated against faculty's yearsNumbers (e.g., 1-4 for 4-year programs)</li>
 * </ol>
 *
 * <p>This data is typically seeded via database migrations and rarely changes.
 * Updates are handled by admins through database scripts or admin tools.
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.academic;
