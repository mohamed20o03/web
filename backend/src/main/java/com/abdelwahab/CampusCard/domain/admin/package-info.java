/**
 * Administrative operations domain.
 * Handles user approval workflow, dashboard statistics, and user management.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Controller:</strong> AdminController (requires ADMIN role)</li>
 *   <li><strong>Service:</strong> AdminService with approval and management operations</li>
 *   <li><strong>DTOs:</strong> Dashboard stats, user approval responses, approval decisions</li>
 * </ul>
 *
 * <p>Admin responsibilities:
 * <ul>
 *   <li><strong>User Approval:</strong> Review pending users, verify emails, approve/reject</li>
 *   <li><strong>Dashboard:</strong> View system statistics and pending tasks</li>
 *   <li><strong>User Management:</strong> Search users, change roles, view all users</li>
 *   <li><strong>Content Moderation:</strong> Manage banned words, review flagged content</li>
 * </ul>
 *
 * <p>Approval workflow:
 * <ol>
 *   <li>View pending users list with email verification status</li>
 *   <li>Send email verification link if not verified</li>
 *   <li>Wait for user to verify email (click link)</li>
 *   <li>Compare profile photo with national ID scan for identity validation</li>
 *   <li>Approve if match (APPROVED status) or reject if mismatch (REJECTED status)</li>
 * </ol>
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.admin;
