package com.abdelwahab.CampusCard.domain.admin.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abdelwahab.CampusCard.domain.admin.dto.AdminDashboardStats;
import com.abdelwahab.CampusCard.domain.moderation.dto.BannedWordResponse;
import com.abdelwahab.CampusCard.domain.moderation.dto.FlaggedContentResponse;
import com.abdelwahab.CampusCard.domain.admin.dto.UserApprovalResponse;
import com.abdelwahab.CampusCard.domain.moderation.model.BannedWord;
import com.abdelwahab.CampusCard.domain.moderation.model.FlaggedContent;
import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.auth.service.EmailService;
import com.abdelwahab.CampusCard.domain.moderation.repository.BannedWordRepository;
import com.abdelwahab.CampusCard.domain.moderation.repository.FlaggedContentRepository;
import com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.storage.service.MinioService;
import com.abdelwahab.CampusCard.domain.moderation.service.ContentModerationService;
import com.abdelwahab.CampusCard.domain.common.exception.ResourceNotFoundException;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidStateException;
import com.abdelwahab.CampusCard.domain.common.exception.DuplicateResourceException;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidTokenException;
import com.abdelwahab.CampusCard.domain.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

/**
 * Service responsible for administrative operations and user management.
 * Handles the complete user approval workflow, content moderation, and system statistics.
 *
 * <p>Key administrative functions:
 * <ul>
 *   <li>User approval workflow (pending users, email verification, approval/rejection)</li>
 *   <li>User role management (promote/demote users to ADMIN)</li>
 *   <li>Dashboard statistics (user counts by status, pending approvals, flagged content)</li>
 *   <li>Content moderation (manage banned words, review flagged content)</li>
 *   <li>User search and filtering capabilities</li>
 * </ul>
 *
 * <p>Approval workflow:
 * <ol>
 *   <li>New user registers → PENDING status</li>
 *   <li>Admin sends email verification request</li>
 *   <li>User verifies email → emailVerified = true</li>
 *   <li>Admin reviews profile photo vs national ID scan</li>
 *   <li>Admin approves → APPROVED status, OR rejects → REJECTED status</li>
 * </ol>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final BannedWordRepository bannedWordRepository;
    private final FlaggedContentRepository flaggedContentRepository;
    
    /**
     * Retrieves all users with PENDING status awaiting admin approval.
     * Returns detailed information for each pending user including verification status and photos.
     *
     * <p>Response includes:
     * <ul>
     *   <li>User basic info (ID, name, email, national ID)</li>
     *   <li>Email verification status</li>
     *   <li>Profile photo URL</li>
     *   <li>National ID scan URL</li>
     *   <li>Academic info (faculty, department, year)</li>
     *   <li>Registration date</li>
     * </ul>
     *
     * @return list of pending users with complete approval information
     */
    public List<UserApprovalResponse> getPendingApprovals() {
        List<User> pendingUsers = userRepository.findByStatus(User.Status.PENDING);
        return pendingUsers.stream()
                .map(this::buildUserApprovalResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all users in the system regardless of status.
     * Used for bulk user management and system-wide user overview.
     *
     * @return list of all users with complete information
     */
    public List<UserApprovalResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::buildUserApprovalResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves detailed information for a specific user for approval review.
     * Used when admin needs to review individual user before making approval decision.
     *
     * @param userId the ID of the user to retrieve
     * @return complete user information including verification status and photos
     * @throws RuntimeException if user not found
     */
    public UserApprovalResponse getUserForApproval(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return buildUserApprovalResponse(user);
    }
    
    /**
     * Approves a pending user after email verification and identity validation.
     * User status changes from PENDING to APPROVED, granting full platform access.
     *
     * <p>Prerequisites:
     * <ul>
     *   <li>User must be in PENDING status</li>
     *   <li>Email must be verified (emailVerified = true)</li>
     * </ul>
     *
     * <p>Side effects:
     * <ul>
     *   <li>Updates user status to APPROVED</li>
     *   <li>Clears any previous rejection reason</li>
     *   <li>User gains access to full profile features</li>
     * </ul>
     *
     * @param userId the ID of the user to approve
     * @param adminId the ID of the admin making the approval decision
     * @return updated user information after approval
     * @throws RuntimeException if user not found
     * @throws RuntimeException if user is not in PENDING status
     * @throws RuntimeException if email is not verified
     */
    @Transactional
    public UserApprovalResponse approveUser(Integer userId, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new InvalidStateException("User is not in pending status");
        }
        
        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new InvalidStateException("Cannot approve user with unverified email. Please verify email first.");
        }
        
        user.setStatus(User.Status.APPROVED);
        user.setRejectionReason(null); // Clear any previous rejection reason
        User savedUser = userRepository.save(user);
        
        return buildUserApprovalResponse(savedUser);
    }
    
    /**
     * Rejects a pending user with optional reason for rejection.
     * User status changes from PENDING to REJECTED, restricting platform access.
     *
     * <p>Prerequisites:
     * <ul>
     *   <li>User must be in PENDING status</li>
     * </ul>
     *
     * <p>Side effects:
     * <ul>
     *   <li>Updates user status to REJECTED</li>
     *   <li>Stores rejection reason for admin reference</li>
     *   <li>User profile remains private and inaccessible</li>
     * </ul>
     *
     * @param userId the ID of the user to reject
     * @param adminId the ID of the admin making the rejection decision
     * @param reason optional reason for rejection (e.g., "Photo mismatch", "Invalid documents")
     * @return updated user information after rejection
     * @throws RuntimeException if user not found
     * @throws RuntimeException if user is not in PENDING status
     */
    @Transactional
    public UserApprovalResponse rejectUser(Integer userId, Integer adminId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (user.getStatus() != User.Status.PENDING) {
            throw new InvalidStateException("User is not in pending status");
        }
        
        user.setStatus(User.Status.REJECTED);
        user.setRejectionReason(reason);
        User savedUser = userRepository.save(user);
        
        return buildUserApprovalResponse(savedUser);
    }
    
    /**
     * Generates and sends an email verification token to user's registered email.
     * Token is valid for 24 hours and required before user approval.
     *
     * <p>Process:
     * <ol>
     *   <li>Generates UUID verification token</li>
     *   <li>Stores token in user record</li>
     *   <li>Sends email with verification link</li>
     * </ol>
     *
     * <p>Note: In development mode, the token is returned in the response
     * for testing purposes. In production, it should only be sent via email.
     *
     * @param userId the ID of the user to send verification email to
     * @return the generated verification token (for testing mode only)
     * @throws RuntimeException if user not found
     * @throws RuntimeException if email is already verified
     */
    @Transactional
    public String sendEmailVerification(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (user.getEmailVerified()) {
            throw new InvalidStateException("Email is already verified");
        }
        
        // Generate verification token
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationSentAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Send email with verification link
        try {
            emailService.sendVerificationEmail(user.getEmail(), userId, token);
        } catch (Exception e) {
            // If email fails in production, log but still return token for testing
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
        
        return token;
    }
    
    /**
     * Verify user's email with token.
     * 
     * @param userId User ID
     * @param token Verification token
     */
    @Transactional
    public void verifyEmail(Integer userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getEmailVerified()) {
            throw new InvalidStateException("Email is already verified");
        }
        
        if (user.getEmailVerificationToken() == null || !user.getEmailVerificationToken().equals(token)) {
            throw new InvalidTokenException("Invalid verification token");
        }
        
        // Check if token is expired (24 hours)
        if (user.getEmailVerificationSentAt() != null && 
            user.getEmailVerificationSentAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Verification token has expired");
        }
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null); // Clear token after verification
        userRepository.save(user);
    }
    
    /**
     * Get dashboard statistics for admin overview.
     */
    public AdminDashboardStats getDashboardStats() {
        return AdminDashboardStats.builder()
                .totalUsers(userRepository.count())
                .pendingApprovals(userRepository.countByStatus(User.Status.PENDING))
                .approvedUsers(userRepository.countByStatus(User.Status.APPROVED))
                .rejectedUsers(userRepository.countByStatus(User.Status.REJECTED))
                .studentsCount(userRepository.countByRole(User.Role.STUDENT))
                .adminsCount(userRepository.countByRole(User.Role.ADMIN))
                .verifiedEmails(userRepository.countByEmailVerified(true))
                .unverifiedEmails(userRepository.countByEmailVerified(false))
                .build();
    }
    
    /**
     * Build UserApprovalResponse from User entity.
     */
    private UserApprovalResponse buildUserApprovalResponse(User user) {
        Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String registrationDate = user.getCreatedAt() != null ? 
                user.getCreatedAt().format(formatter) : null;
        
        return UserApprovalResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .nationalId(user.getNationalId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .status(user.getStatus().getValue())
                .role(user.getRole().getValue())
                .year(user.getYear())
                .faculty(user.getFaculty().getName())
                .department(user.getDepartment().getName())
                .profilePhotoUrl(profile != null ? profile.getProfilePhoto() : null)
                .nationalIdScanUrl(user.getNationalIdScan())
                .registrationDate(registrationDate)
                .build();
    }

    /**
     * Get all banned words for content moderation.
     */
    public List<BannedWordResponse> getAllBannedWords() {
        List<BannedWord> words = bannedWordRepository.findAllByOrderByWordAsc();
        return words.stream()
                .map(word -> BannedWordResponse.builder()
                        .id(word.getId())
                        .word(word.getWord())
                        .addedAt(word.getAddedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Add a new banned word.
     */
    @Transactional
    public BannedWordResponse addBannedWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            throw new InvalidStateException("Word cannot be empty");
        }
        
        String normalizedWord = word.trim().toLowerCase();
        
        // Check if word already exists
        BannedWord existing = bannedWordRepository.findByWord(normalizedWord);
        if (existing != null) {
            throw new DuplicateResourceException("BannedWord", "word", normalizedWord);
        }
        
        BannedWord bannedWord = BannedWord.builder()
                .word(normalizedWord)
                .build();
        
        BannedWord saved = bannedWordRepository.save(bannedWord);
        
        return BannedWordResponse.builder()
                .id(saved.getId())
                .word(saved.getWord())
                .addedAt(saved.getAddedAt())
                .build();
    }

    /**
     * Delete a banned word.
     */
    @Transactional
    public void deleteBannedWord(Integer wordId) {
        BannedWord word = bannedWordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Banned word not found"));
        bannedWordRepository.delete(word);
    }

    /**
     * Get all flagged content for admin review.
     */
    public List<FlaggedContentResponse> getFlaggedContent() {
        List<FlaggedContent> content = flaggedContentRepository.findAllByOrderByFlaggedAtDesc();
        return content.stream()
                .map(fc -> FlaggedContentResponse.builder()
                        .id(fc.getId())
                        .userId(fc.getUser().getId())
                        .userEmail(fc.getUser().getEmail())
                        .userName(fc.getUser().getFirstName() + " " + fc.getUser().getLastName())
                        .content(fc.getContent())
                        .flaggedAt(fc.getFlaggedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Change a user's role (e.g., promote student to admin or demote admin to student).
     * 
     * @param userId User whose role is being changed
     * @param newRole New role (STUDENT or ADMIN)
     * @param adminId Admin making the change
     * @return Updated user response
     */
    @Transactional
    public UserApprovalResponse changeUserRole(Integer userId, String newRole, Integer adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Prevent admin from changing their own role
        if (userId.equals(adminId)) {
            throw new UnauthorizedException("Cannot change your own role");
        }
        
        // Validate role
        User.Role role;
        try {
            role = User.Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Invalid role. Must be STUDENT or ADMIN");
        }
        
        // Update role
        user.setRole(role);
        User savedUser = userRepository.save(user);
        
        return buildUserApprovalResponse(savedUser);
    }
}
