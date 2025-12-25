package com.abdelwahab.CampusCard.domain.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.abdelwahab.CampusCard.domain.academic.model.Department;
import com.abdelwahab.CampusCard.domain.academic.model.Faculty;
import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository;
import com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;

/**
 * Configuration class responsible for initializing the default admin user.
 * This runs on application startup and creates an admin account if it doesn't exist.
 * 
 * <p>Admin credentials are loaded from environment variables for security.
 * Required environment variables:
 * <ul>
 *   <li>ADMIN_EMAIL - Admin user's email address</li>
 *   <li>ADMIN_PASSWORD - Admin user's password (will be hashed with BCrypt)</li>
 *   <li>ADMIN_FIRST_NAME - Admin user's first name</li>
 *   <li>ADMIN_LAST_NAME - Admin user's last name</li>
 *   <li>ADMIN_NATIONAL_ID - Admin user's national ID</li>
 *   <li>ADMIN_FACULTY_ID - Faculty ID to associate with admin</li>
 *   <li>ADMIN_DEPARTMENT_ID - Department ID to associate with admin</li>
 * </ul>
 * 
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class AdminUserInitializer {
    
    @Value("${app.admin.email}")
    private String adminEmail;
    
    @Value("${app.admin.password}")
    private String adminPassword;
    
    @Value("${app.admin.firstName}")
    private String adminFirstName;
    
    @Value("${app.admin.lastName}")
    private String adminLastName;
    
    @Value("${app.admin.nationalId}")
    private String adminNationalId;
    
    @Value("${app.admin.facultyId}")
    private Integer adminFacultyId;
    
    @Value("${app.admin.departmentId}")
    private Integer adminDepartmentId;
    
    /**
     * Creates a CommandLineRunner bean that initializes the admin user on application startup.
     * 
     * @param userRepository repository for user operations
     * @param profileRepository repository for profile operations
     * @param facultyRepository repository for faculty operations
     * @param departmentRepository repository for department operations
     * @return CommandLineRunner that performs admin user initialization
     */
    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository,
                                             ProfileRepository profileRepository,
                                             FacultyRepository facultyRepository,
                                             DepartmentRepository departmentRepository) {
        return args -> {
            // Check if admin user already exists
            if (userRepository.findByEmail(adminEmail) == null) {
                Faculty faculty = facultyRepository.findById(adminFacultyId).orElse(null);
                Department department = departmentRepository.findById(adminDepartmentId).orElse(null);
                
                if (faculty != null && department != null) {
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String hashedPassword = encoder.encode(adminPassword);
                    
                    // Create admin user with ADMIN role and APPROVED status
                    User admin = User.builder()
                        .email(adminEmail)
                        .password(hashedPassword)
                        .firstName(adminFirstName)
                        .lastName(adminLastName)
                        .birthDate(java.time.LocalDate.of(2000, 1, 1)) // Default birth date
                        .nationalId(adminNationalId)
                        .nationalIdScan("ADMIN_DEFAULT") // Default value for admin users
                        .role(User.Role.ADMIN)
                        .status(User.Status.APPROVED)
                        .emailVerified(true) // Admin email is pre-verified
                        .year(1) // Default year (not relevant for admin)
                        .faculty(faculty)
                        .department(department)
                        .build();
                    admin = userRepository.save(admin);
                    
                    // Create default private profile for admin
                    Profile profile = Profile.builder()
                        .user(admin)
                        .profilePhoto(null) // No profile photo initially
                        .bio("System Administrator")
                        .visibility(Profile.Visibility.PRIVATE)
                        .build();
                    profileRepository.save(profile);
                    
                    System.out.println("Admin user created successfully: " + adminEmail);
                } else {
                    System.err.println("Failed to create admin user: Faculty or Department not found");
                }
            }
        };
    }
}
