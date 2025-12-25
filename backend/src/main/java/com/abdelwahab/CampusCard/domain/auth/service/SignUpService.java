package com.abdelwahab.CampusCard.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abdelwahab.CampusCard.domain.auth.dto.SignUpRequest;
import com.abdelwahab.CampusCard.domain.auth.dto.SignUpResponse;
import com.abdelwahab.CampusCard.domain.academic.model.Department;
import com.abdelwahab.CampusCard.domain.academic.model.Faculty;
import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository;
import com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.storage.service.MinioService;
import com.abdelwahab.CampusCard.domain.common.exception.DuplicateResourceException;
import com.abdelwahab.CampusCard.domain.common.exception.ResourceNotFoundException;
import com.abdelwahab.CampusCard.domain.common.exception.InvalidStateException;

import lombok.RequiredArgsConstructor;

/**
 * Service responsible for user registration and account creation.
 * Handles the complete signup workflow including validation, file upload, and profile creation.
 *
 * <p>Registration process:
 * <ul>
 *   <li>Validates email uniqueness and PSU domain</li>
 *   <li>Validates national ID format and uniqueness</li>
 *   <li>Verifies faculty and department existence</li>
 *   <li>Validates academic year against faculty year range</li>
 *   <li>Uploads national ID scan to MinIO storage</li>
 *   <li>Creates user account with PENDING status</li>
 *   <li>Initializes empty profile for user</li>
 * </ul>
 *
 * <p>New users are created with PENDING status and require admin approval
 * after email verification before gaining full platform access.
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SignUpService {
    
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioService minioService;

    /**
     * Registers a new user account with complete validation and setup.
     *
     * <p>Validation steps:
     * <ul>
     *   <li>Checks email uniqueness</li>
     *   <li>Checks national ID uniqueness</li>
     *   <li>Verifies faculty exists</li>
     *   <li>Verifies department exists and belongs to selected faculty</li>
     *   <li>Validates year is within faculty's year range (1 to faculty.yearsNumbers)</li>
     * </ul>
     *
     * <p>Setup steps:
     * <ul>
     *   <li>Hashes password using BCrypt</li>
     *   <li>Uploads national ID scan to MinIO</li>
     *   <li>Creates user with PENDING status and STUDENT role</li>
     *   <li>Creates empty profile for user</li>
     * </ul>
     *
     * @param request the signup request containing user details, academic info, and national ID scan
     * @return SignUpResponse with success message and user email
     * @throws DuplicateResourceException if email or national ID is already registered
     * @throws ResourceNotFoundException if faculty or department not found
     * @throws InvalidStateException if department doesn't belong to faculty or year is invalid
     */
    @Transactional
    public SignUpResponse registerUser(SignUpRequest request) {
        // Validation is handled by @Valid annotation in controller

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Check if national ID already exists
        if (userRepository.findByNationalId(request.getNationalId()) != null) {
            throw new DuplicateResourceException("User", "nationalId", request.getNationalId());
        }

        // Validate faculty exists
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
            .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", request.getFacultyId()));

        // Validate department exists and belongs to the faculty
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        
        if (!department.getFaculty().getId().equals(faculty.getId())) {
            throw new InvalidStateException("Department does not belong to the selected faculty");
        }

        // Validate year is within faculty's year range
        if (request.getYear() < 1 || request.getYear() > faculty.getYearsNumbers()) {
            throw new InvalidStateException("Invalid year for the selected faculty");
        }

        // Create new user with temporary national ID scan placeholder
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .birthDate(request.getDateOfBirth())
            .nationalId(request.getNationalId())
            .nationalIdScan("temp") // Temporary placeholder
            .year(request.getYear())
            .faculty(faculty)
            .department(department)
            .build();

        User savedUser = userRepository.save(user);

        // Upload national ID scan to MinIO using the saved user's ID
        String scanUrl = minioService.uploadNationalIdScan(savedUser.getId(), request.getNationalIdScan());
        savedUser.setNationalIdScan(scanUrl);
        userRepository.save(savedUser);

        // Create default profile for the user
        Profile profile = Profile.builder()
            .user(savedUser)
            .visibility(Profile.Visibility.PUBLIC)
            .build();
        profileRepository.save(profile);

        return SignUpResponse.builder()
            .id(savedUser.getId())
            .email(savedUser.getEmail())
            .status(savedUser.getStatus().name())
            .message("User registered successfully. Awaiting admin approval.")
            .build();
    }
}
