package com.abdelwahab.CampusCard.domain.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abdelwahab.CampusCard.domain.admin.dto.AdminDashboardStats;
import com.abdelwahab.CampusCard.domain.admin.dto.ApprovalDecisionRequest;
import com.abdelwahab.CampusCard.domain.admin.dto.UserApprovalResponse;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.admin.service.AdminService;
import com.abdelwahab.CampusCard.domain.common.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for administrative operations and user management.
 * All endpoints require ADMIN role authentication.
 *
 * <p>Base path: {@code /api/admin}
 *
 * <p>Key administrative features:
 * <ul>
 *   <li><strong>Dashboard:</strong> System statistics and metrics</li>
 *   <li><strong>User Management:</strong> View, approve, reject, and search users</li>
 *   <li><strong>Email Verification:</strong> Send verification links to pending users</li>
 *   <li><strong>Role Management:</strong> Promote/demote users to ADMIN role</li>
 *   <li><strong>Content Moderation:</strong> Manage banned words and flagged content</li>
 * </ul>
 *
 * <p>Approval workflow endpoints:
 * <ol>
 *   <li>{@code GET /users/pending} - View users awaiting approval</li>
 *   <li>{@code POST /users/{id}/verify-email} - Send verification email</li>
 *   <li>{@code GET /users/{id}} - Review specific user details</li>
 *   <li>{@code POST /users/{id}/approve} - Approve user (requires verified email)</li>
 *   <li>{@code POST /users/{id}/reject} - Reject user with reason</li>
 * </ol>
 *
 * <p>Security: All endpoints validate JWT token and check for ADMIN role.
 * Unauthorized requests return 403 FORBIDDEN.
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Operations", description = "Administrative endpoints for user management, approvals, and system statistics (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    
    private final AdminService adminService;
    
    @Value("${app.testing.mode:true}")
    private boolean testingMode;
    
    /**
     * Retrieves administrative dashboard statistics.
     * Provides overview of system state and pending tasks.
     *
     * <p>Statistics included:
     * <ul>
     *   <li>Total users count</li>
     *   <li>User counts by status (PENDING, APPROVED, REJECTED)</li>
     *   <li>User counts by role (STUDENT, ADMIN)</li>
     *   <li>Email verification statistics</li>
     *   <li>Flagged content count requiring review</li>
     *   <li>Banned words count</li>
     * </ul>
     *
     * @return AdminDashboardStats with current system metrics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        try {
            AdminDashboardStats stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves all users with PENDING status awaiting admin approval.
     * Essential for admin review and approval workflow.
     *
     * <p>Each user entry includes:
     * <ul>
     *   <li>Basic info (ID, email, name, national ID)</li>
     *   <li>Email verification status (must be verified before approval)</li>
     *   <li>Profile photo URL (for visual identity verification)</li>
     *   <li>National ID scan URL (for comparison with profile photo)</li>
     *   <li>Academic details (faculty, department, year)</li>
     *   <li>Registration timestamp</li>
     * </ul>
     *
     * <p>Admin workflow:
     * <ol>
     *   <li>Review pending users list</li>
     *   <li>Verify email if not already verified</li>
     *   <li>Compare profile photo with national ID scan</li>
     *   <li>Approve if match, reject if mismatch or suspicious</li>
     * </ol>
     *
     * @return list of pending users with complete approval information
     */
    @GetMapping("/users/pending")
    public ResponseEntity<List<UserApprovalResponse>> getPendingApprovals() {
        try {
            List<UserApprovalResponse> pending = adminService.getPendingApprovals();
            return ResponseEntity.ok(pending);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users
     * Get all users in the system for bulk management.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserApprovalResponse>> getAllUsers() {
        try {
            List<UserApprovalResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/{userId}
     * Get detailed information about a specific user for approval review.
     * 
     * Use this endpoint to:
     * 1. Check if email is verified (emailVerified field)
     * 2. Download profile photo and national ID scan
     * 3. Manually compare photos to verify identity
     * 4. Make approval decision
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserForApproval(@PathVariable Integer userId) {
        try {
            UserApprovalResponse user = adminService.getUserForApproval(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * POST /api/admin/users/approve-reject
     * Approve or reject a user registration.
     * 
     * Admin workflow:
     * 1. Check emailVerified = true (if false, use /verify-email endpoint first)
     * 2. Compare profilePhotoUrl with nationalIdScanUrl
     * 3. Verify person in profile photo matches person in ID scan
     * 4. Make decision: approve=true or approve=false
     * 
     * Request body:
     * {
     *   "userId": 123,
     *   "approved": true,  // true to approve, false to reject
     *   "rejectionReason": "Photo does not match ID" // optional, for rejections
     * }
     */
    @PostMapping("/users/approve-reject")
    public ResponseEntity<?> approveOrRejectUser(@Valid @RequestBody ApprovalDecisionRequest request) {
        try {
            Integer adminId = getCurrentUserId();
            UserApprovalResponse response;
            
            if (request.getApproved()) {
                response = adminService.approveUser(request.getUserId(), adminId);
            } else {
                response = adminService.rejectUser(
                        request.getUserId(), 
                        adminId, 
                        request.getRejectionReason()
                );
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * POST /api/admin/users/{userId}/verify-email
     * Send email verification to user.
     * 
     * Use this before approving a user if emailVerified = false.
     * Sends an email with verification link to the user.
     * 
     * TESTING MODE (app.testing.mode=true): Returns token in response for manual verification
     * PRODUCTION MODE (app.testing.mode=false): Only sends email, no token returned
     */
    @PostMapping("/users/{userId}/send-verification")
    public ResponseEntity<?> sendEmailVerification(@PathVariable Integer userId) {
        try {
            String token = adminService.sendEmailVerification(userId);
            
            if (testingMode) {
                // Testing: return token for manual verification
                return ResponseEntity.ok(Map.of(
                        "message", "Verification email sent (testing mode)",
                        "token", token,
                        "userId", userId
                ));
            } else {
                // Production: only confirm email was sent
                return ResponseEntity.ok(Map.of(
                        "message", "Verification email sent successfully",
                        "info", "User will receive email with verification link"
                ));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * POST /api/admin/users/{userId}/verify-email/{token}
     * Verify user's email with token.
     * 
     * Normally user would click link in email, but admin can verify manually.
     */
    @PostMapping("/users/{userId}/verify-email/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable Integer userId, @PathVariable String token) {
        try {
            adminService.verifyEmail(userId, token);
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    /**
     * GET /api/admin/banned-words
     * Get all banned words for content moderation.
     */
    @GetMapping("/banned-words")
    public ResponseEntity<List<com.abdelwahab.CampusCard.domain.moderation.dto.BannedWordResponse>> getAllBannedWords() {
        try {
            List<com.abdelwahab.CampusCard.domain.moderation.dto.BannedWordResponse> words = 
                    adminService.getAllBannedWords();
            return ResponseEntity.ok(words);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/admin/banned-words
     * Add a new banned word to the moderation list.
     */
    @PostMapping("/banned-words")
    public ResponseEntity<?> addBannedWord(
            @Valid @RequestBody com.abdelwahab.CampusCard.domain.moderation.dto.AddBannedWordRequest request) {
        try {
            com.abdelwahab.CampusCard.domain.moderation.dto.BannedWordResponse word = 
                    adminService.addBannedWord(request.getWord());
            return ResponseEntity.ok(word);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/banned-words/{wordId}
     * Remove a banned word from the moderation list.
     */
    @org.springframework.web.bind.annotation.DeleteMapping("/banned-words/{wordId}")
    public ResponseEntity<?> deleteBannedWord(@PathVariable Integer wordId) {
        try {
            adminService.deleteBannedWord(wordId);
            return ResponseEntity.ok(Map.of("message", "Banned word deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/flagged-content
     * Get all flagged content for admin review.
     */
    @GetMapping("/flagged-content")
    public ResponseEntity<List<com.abdelwahab.CampusCard.domain.moderation.dto.FlaggedContentResponse>> getFlaggedContent() {
        try {
            List<com.abdelwahab.CampusCard.domain.moderation.dto.FlaggedContentResponse> content = 
                    adminService.getFlaggedContent();
            return ResponseEntity.ok(content);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/admin/users/{userId}/change-role
     * Change a user's role (e.g., make a student an admin).
     * Only admins can change roles.
     */
    @PostMapping("/users/{userId}/change-role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Integer userId,
            @Valid @RequestBody com.abdelwahab.CampusCard.domain.admin.dto.ChangeRoleRequest request) {
        try {
            Integer adminId = getCurrentUserId();
            UserApprovalResponse response = adminService.changeUserRole(userId, request.getRole(), adminId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get current admin's user ID from SecurityContext.
     */
    private Integer getCurrentUserId() {
        org.springframework.security.core.Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        throw new UnauthorizedException("User not authenticated");
    }
}
