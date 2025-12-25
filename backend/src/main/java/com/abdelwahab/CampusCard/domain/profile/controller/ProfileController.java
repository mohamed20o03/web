package com.abdelwahab.CampusCard.domain.profile.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abdelwahab.CampusCard.domain.storage.dto.NationalIdScanResponse;
import com.abdelwahab.CampusCard.domain.storage.dto.ProfilePhotoResponse;
import com.abdelwahab.CampusCard.domain.profile.dto.ProfileResponse;
import com.abdelwahab.CampusCard.domain.profile.dto.UpdateProfileRequest;
import com.abdelwahab.CampusCard.domain.profile.dto.UpdateVisibilityRequest;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.profile.service.ProfileService;
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

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Validated
@Tag(name = "Profile Management", description = "User profile operations including viewing, updating, and file uploads")
public class ProfileController {
    
    private final ProfileService profileService;

    /**
     * GET /api/profile - Get current user's profile
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getCurrentUserProfile() {
        try {
            Integer userId = getCurrentUserId();
            ProfileResponse profile = profileService.getCurrentUserProfile(userId);
            return ResponseEntity.ok(profile); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

    /**
     * GET /api/profile/{userId} - Get another user's profile (respects visibility)
     * This endpoint is accessible to both authenticated and unauthenticated users.
     * Visibility is enforced in the service layer:
     * - PUBLIC: visible to everyone
     * - STUDENTS_ONLY: visible to authenticated users only
     * - PRIVATE: visible only to profile owner and admins
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        try {
            // Get current user info (may be null if not authenticated)
            Integer currentUserId = null;
            String currentUserRole = null;
            
            try {
                currentUserId = getCurrentUserId();
                currentUserRole = getCurrentUserRole();
            } catch (RuntimeException e) {
                // User is not authenticated - this is allowed for public profiles
                // currentUserId and currentUserRole remain null
            }
            
            ProfileResponse profile = profileService.getUserProfile(userId, currentUserId, currentUserRole);
            return ResponseEntity.ok(profile); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/profile/public-students
     * Get all users in the system for bulk management.
     */
    @GetMapping("/public-students")
    public ResponseEntity<List<ProfileResponse>> getPublicStudents() {
        try {
            List<ProfileResponse> users = profileService.getPublicApprovedStudents();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/profile - Update current user's profile
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            Integer userId = getCurrentUserId();
            ProfileResponse profile = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(profile); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/profile/photo - Upload profile photo
     */
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            Integer userId = getCurrentUserId();
            ProfilePhotoResponse response = profileService.uploadProfilePhoto(userId, file);
            return ResponseEntity.ok(response); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/profile/national-id-scan - Upload national ID scan
     */
    @PostMapping(value = "/national-id-scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadNationalIdScan(@RequestParam("file") MultipartFile file) {
        try {
            Integer userId = getCurrentUserId();
            NationalIdScanResponse response = profileService.uploadNationalIdScan(userId, file);
            return ResponseEntity.ok(response); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/profile/visibility - Update profile visibility (PUBLIC/PRIVATE)
     */
    @PutMapping("/visibility")
    public ResponseEntity<?> updateVisibility(@Valid @RequestBody UpdateVisibilityRequest request) {
        try {
            Integer userId = getCurrentUserId();
            ProfileResponse profile = profileService.updateVisibility(userId, request.getVisibility());
            return ResponseEntity.ok(profile); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get current authenticated user's ID from SecurityContext
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        throw new UnauthorizedException("User not authenticated");
    }

    /**
     * Get current authenticated user's role from SecurityContext
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getRole().toString();
        }
        return null;
    }

    /**
     * Error response DTO
     */
    private record ErrorResponse(String message) {}
}
