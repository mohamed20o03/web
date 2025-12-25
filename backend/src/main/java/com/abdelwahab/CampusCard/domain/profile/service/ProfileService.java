package com.abdelwahab.CampusCard.domain.profile.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.abdelwahab.CampusCard.domain.storage.service.MinioService;
import com.abdelwahab.CampusCard.domain.moderation.service.ContentModerationService;
import com.abdelwahab.CampusCard.domain.storage.dto.NationalIdScanResponse;
import com.abdelwahab.CampusCard.domain.storage.dto.ProfilePhotoResponse;
import com.abdelwahab.CampusCard.domain.profile.dto.ProfileResponse;
import com.abdelwahab.CampusCard.domain.profile.dto.UpdateProfileRequest;
import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository;
import com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.common.exception.ResourceNotFoundException;
import com.abdelwahab.CampusCard.domain.common.exception.UnauthorizedException;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidStateException;

import lombok.RequiredArgsConstructor;

/**
 * Service responsible for user profile management and visibility control.
 * Handles profile CRUD operations, photo uploads, and access control based on visibility settings.
 *
 * <p>Profile visibility levels:
 * <ul>
 *   <li><strong>PUBLIC:</strong> Visible to all authenticated students in directory</li>
 *   <li><strong>STUDENTS_ONLY:</strong> Visible only to other approved students</li>
 *   <li><strong>PRIVATE:</strong> Hidden from directory, only accessible by profile owner</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Public student directory with approved students only</li>
 *   <li>Profile photo upload with validation</li>
 *   <li>Bio and social links management</li>
 *   <li>Content moderation for profile text fields</li>
 *   <li>Visibility-based access control</li>
 * </ul>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final MinioService minioService;
    private final ContentModerationService contentModerationService;

    /**
     * Retrieves all public profiles of approved students for the student directory.
     * Only returns students with APPROVED status, STUDENT role, and PUBLIC visibility.
     *
     * <p>Filtering criteria:
     * <ul>
     *   <li>User status = APPROVED</li>
     *   <li>User role = STUDENT</li>
     *   <li>Profile visibility = PUBLIC</li>
     * </ul>
     *
     * @return list of public student profiles with basic information and photos
     */
    public List<ProfileResponse> getPublicApprovedStudents() {

    List<Profile> profiles = profileRepository.findPublicApprovedStudents(
            User.Status.APPROVED,
            User.Role.STUDENT,
            Profile.Visibility.PUBLIC
    );

    return profiles.stream()
            .map(profile -> buildProfileResponse(profile.getUser(), profile))
            .toList();
}


    /**
     * Get current user's profile
     */
    public ProfileResponse getCurrentUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        return buildProfileResponse(user, profile);
    }

    /**
     * Get another user's profile (respects visibility)
     * @param targetUserId The ID of the user whose profile is being requested
     * @param currentUserId The ID of the current user (null if not authenticated)
     * @param currentUserRole The role of the current user (null if not authenticated)
     */
    public ProfileResponse getUserProfile(Integer targetUserId, Integer currentUserId, String currentUserRole) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", targetUserId));
        
        // Check visibility permissions
        if (!canViewProfile(profile, targetUserId, currentUserId, currentUserRole)) {
            throw new UnauthorizedException("Access denied: You don't have permission to view this profile");
        }
        
        return buildProfileResponse(targetUser, profile);
    }

    /**
     * Update current user's profile
     */
    @Transactional
    public ProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        // Content moderation: Check for banned words (profile fields only)
        java.util.Map<String, String> fieldsToCheck = new java.util.HashMap<>();
        if (request.getBio() != null) fieldsToCheck.put("bio", request.getBio());
        if (request.getInterests() != null) fieldsToCheck.put("interests", request.getInterests());
        if (request.getLinkedin() != null) fieldsToCheck.put("linkedin", request.getLinkedin());
        if (request.getGithub() != null) fieldsToCheck.put("github", request.getGithub());

        java.util.Map<String, List<String>> violations = contentModerationService.validateFields(fieldsToCheck);

        if (!violations.isEmpty()) {
            // Log violations for admin review
            for (java.util.Map.Entry<String, List<String>> violation : violations.entrySet()) {
                contentModerationService.logViolation(
                    userId,
                    violation.getKey(),
                    fieldsToCheck.get(violation.getKey()),
                    violation.getValue()
                );
            }
            // Throw exception to prevent update
            String violationFields = String.join(", ", violations.keySet());
            String bannedWords = violations.values().stream()
                    .flatMap(List::stream)
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new InvalidStateException(
                String.format("Content moderation violation: Inappropriate language detected in field(s): %s. Banned words: %s", 
                    violationFields, bannedWords)
            );
        }

        // --- Update user fields if provided ---
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getNationalId() != null && !request.getNationalId().isBlank()) {
            user.setNationalId(request.getNationalId());
        }
        if (request.getNationalIdScan() != null) {
            user.setNationalIdScan(request.getNationalIdScan());
        }
        if (request.getFacultyId() != null) {
            // Only update if faculty exists
            var faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
            user.setFaculty(faculty);
        }
        if (request.getDepartmentId() != null) {
            var department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
        }
        if (request.getYear() != null) {
            user.setYear(request.getYear());
        }

        // --- Update profile fields if provided ---
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            profile.setPhone(request.getPhone());
        }
        if (request.getLinkedin() != null) {
            profile.setLinkedin(request.getLinkedin());
        }
        if (request.getGithub() != null) {
            profile.setGithub(request.getGithub());
        }
        if (request.getInterests() != null) {
            profile.setInterests(request.getInterests());
        }
        if (request.getVisibility() != null && !request.getVisibility().isBlank()) {
            profile.setVisibility(Profile.Visibility.valueOf(request.getVisibility()));
        }

        // --- Resubmit for review: set status to PENDING and clear rejectionReason ---
        user.setStatus(User.Status.PENDING);
        user.setRejectionReason(null);

        userRepository.save(user);
        Profile updatedProfile = profileRepository.save(profile);
        return buildProfileResponse(user, updatedProfile);
    }

    /**
     * Upload profile photo to MinIO
     */
    @Transactional
    public ProfilePhotoResponse uploadProfilePhoto(Integer userId, MultipartFile file) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Delete old photo if exists
        if (profile.getProfilePhoto() != null && !profile.getProfilePhoto().isEmpty()) {
            String oldObjectName = minioService.extractObjectName(profile.getProfilePhoto());
            if (oldObjectName != null) {
                try {
                    minioService.deleteFile(oldObjectName);
                } catch (Exception e) {
                    // Log but don't fail if old file deletion fails
                }
            }
        }
        
        // Upload new photo to MinIO
        String photoUrl = minioService.uploadProfilePhoto(userId, file);
        
        profile.setProfilePhoto(photoUrl);
        profileRepository.save(profile);
        
        return ProfilePhotoResponse.builder()
                .photoUrl(photoUrl)
                .message("Profile photo uploaded successfully")
                .build();
    }

    /**
     * Update profile visibility
     */
    @Transactional
    public ProfileResponse updateVisibility(Integer userId, String visibility) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update visibility
        Profile.Visibility newVisibility = Profile.Visibility.valueOf(visibility.toUpperCase());
        profile.setVisibility(newVisibility);
        
        Profile updatedProfile = profileRepository.save(profile);
        
        return buildProfileResponse(user, updatedProfile);
    }

    /**
     * Upload national ID scan to MinIO
     */
    @Transactional
    public NationalIdScanResponse uploadNationalIdScan(Integer userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete old scan if exists
        if (user.getNationalIdScan() != null && !user.getNationalIdScan().isEmpty()) {
            String oldObjectName = minioService.extractObjectName(user.getNationalIdScan());
            if (oldObjectName != null) {
                try {
                    minioService.deleteFile(oldObjectName);
                } catch (Exception e) {
                    // Log but don't fail if old file deletion fails
                }
            }
        }
        
        // Upload new scan to MinIO
        String scanUrl = minioService.uploadNationalIdScan(userId, file);
        
        user.setNationalIdScan(scanUrl);
        userRepository.save(user);
        
        return com.abdelwahab.CampusCard.domain.storage.dto.NationalIdScanResponse.builder()
                .scanUrl(scanUrl)
                .message("National ID scan uploaded successfully")
                .build();
    }

    /**
     * Check if current user can view the target profile
     * 
     * Visibility rules:
     * - User can always view their own profile
     * - Admin can view any profile
     * - PENDING/REJECTED users: visible only to admins and the profile owner
     * - APPROVED users with PUBLIC: visible to everyone (including non-authenticated)
     * - APPROVED users with STUDENTS_ONLY: visible to authenticated users (students) and admins
     * - APPROVED users with PRIVATE: visible only to profile owner and admins
     * 
     * @param profile The profile being accessed
     * @param targetUserId The ID of the user whose profile is being requested
     * @param currentUserId The ID of the current user (null if not authenticated)
     * @param currentUserRole The role of the current user (null if not authenticated)
     * @return true if the current user can view the profile, false otherwise
     */
    private boolean canViewProfile(Profile profile, Integer targetUserId, Integer currentUserId, String currentUserRole) {
        // User can always view their own profile
        if (currentUserId != null && targetUserId.equals(currentUserId)) {
            return true;
        }
        
        // Admin can view any profile
        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            return true;
        }
        
        // Get target user to check their approval status
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // PENDING and REJECTED users: visible only to admins and the profile owner
        // (Already checked above for owner and admin, so if we reach here, access is denied)
        if (targetUser.getStatus() != User.Status.APPROVED) {
            return false;
        }
        
        // Check visibility settings for APPROVED users
        Profile.Visibility visibility = profile.getVisibility();
        
        // PUBLIC: visible to everyone (including non-authenticated users)
        if (visibility == Profile.Visibility.PUBLIC) {
            return true;
        }
        
        // STUDENTS_ONLY: visible to authenticated users (students) and admins
        // (Admin already checked above, so here we check if user is authenticated)
        if (visibility == Profile.Visibility.STUDENTS_ONLY) {
            return currentUserId != null; // Must be authenticated
        }
        
        // PRIVATE: visible only to profile owner and admins
        // (Both already checked above, so if we reach here, access is denied)
        if (visibility == Profile.Visibility.PRIVATE) {
            return false;
        }
        
        // Should not reach here, but return false as safe default
        return false;
    }

    /**
     * Build ProfileResponse from User and Profile entities
     */
    private ProfileResponse buildProfileResponse(User user, Profile profile) {
        return ProfileResponse.builder()
            .id(profile.getId())
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .birthDate(user.getBirthDate())
            .profilePhoto(profile.getProfilePhoto())
            .bio(profile.getBio())
            .phone(profile.getPhone())
            .linkedin(profile.getLinkedin())
            .github(profile.getGithub())
            .interests(profile.getInterests())
            .visibility(profile.getVisibility().toString())
            .year(user.getYear())
            .faculty(user.getFaculty() != null ? user.getFaculty().getName() : null)
            .department(user.getDepartment() != null ? user.getDepartment().getName() : null)
            .role(user.getRole().toString())
            .status(user.getStatus() != null ? user.getStatus().toString() : null) 
            .rejectionReason(user.getRejectionReason()) 
            .nationalIdScan(user.getNationalIdScan()) 
            .build();
    }
}
