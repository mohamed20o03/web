package com.abdelwahab.CampusCard;

import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.academic.model.Faculty;
import com.abdelwahab.CampusCard.domain.academic.model.Department;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;
import com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test configuration that creates test users needed for integration tests.
 * Only active in test profile.
 */
@Configuration
@org.springframework.context.annotation.Profile("test")
public class TestUserInitializer {

    @Bean
    public CommandLineRunner createTestUsers(UserRepository userRepository,
                                             ProfileRepository profileRepository,
                                             FacultyRepository facultyRepository,
                                             DepartmentRepository departmentRepository) {
        return args -> {
            // Check if test user already exists
            String testEmail = "test@eng.psu.edu.eg";
            if (userRepository.findByEmail(testEmail) == null) {
                Faculty faculty = facultyRepository.findById(1).orElse(null);
                Department department = departmentRepository.findById(1).orElse(null);
                
                if (faculty != null && department != null) {
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String hashedPassword = encoder.encode("password123");
                    
                    // Create test student user with STUDENT role and APPROVED status
                    User testUser = User.builder()
                        .email(testEmail)
                        .password(hashedPassword)
                        .firstName("Test")
                        .lastName("Student")
                        .birthDate(java.time.LocalDate.of(2000, 5, 15))
                        .nationalId("12345678901234") // This is what the tests expect  
                        .nationalIdScan("TEST_STUDENT_SCAN")
                        .role(User.Role.STUDENT)
                        .status(User.Status.APPROVED) // Must be APPROVED to login
                        .emailVerified(true) // Must be verified to login
                        .year(3) // 3rd year student
                        .faculty(faculty)
                        .department(department)
                        .build();
                    testUser = userRepository.save(testUser);
                    
                    // Create default profile for test user
                    Profile profile = Profile.builder()
                        .user(testUser)
                        .profilePhoto(null)
                        .bio("Test student for integration tests")
                        .visibility(Profile.Visibility.PUBLIC) // Make public for visibility tests
                        .build();
                    profileRepository.save(profile);
                    
                    System.out.println("Test user created successfully: " + testEmail);
                } else {
                    System.err.println("Failed to create test user: Faculty or Department not found");
                }
            }
        };
    }
}