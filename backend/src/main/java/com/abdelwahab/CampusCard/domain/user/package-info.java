/**
 * User domain containing core user entity and repository.
 * Represents the central identity and authentication entity in the system.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Model:</strong> User entity with roles (STUDENT, ADMIN) and status (PENDING, APPROVED, REJECTED)</li>
 *   <li><strong>Repository:</strong> UserRepository for database operations</li>
 * </ul>
 *
 * <p>User lifecycle:
 * <ol>
 *   <li>Created during signup with PENDING status and STUDENT role</li>
 *   <li>Email verified by user clicking verification link</li>
 *   <li>Admin approves → APPROVED status (full access)</li>
 *   <li>Admin rejects → REJECTED status (limited access)</li>
 *   <li>Optionally promoted to ADMIN role for administrative privileges</li>
 * </ol>
 *
 * <p>The User entity is referenced by Profile, FlaggedContent, and serves as
 * the authentication principal throughout the application.
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.user;
