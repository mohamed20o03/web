package com.abdelwahab.CampusCard.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abdelwahab.CampusCard.domain.user.model.User;

/**
 * Spring Data JPA repository for User entity persistence operations.
 * Provides CRUD operations and custom query methods for user management.
 *
 * <p>Custom query methods:
 * <ul>
 *   <li>{@link #findByEmail(String)} - Authentication and uniqueness checks</li>
 *   <li>{@link #findByNationalId(String)} - Alternative authentication method</li>
 *   <li>{@link #findByStatus(User.Status)} - Admin approval workflow</li>
 *   <li>{@link #countByStatus(User.Status)} - Dashboard statistics</li>
 *   <li>{@link #countByRole(User.Role)} - User distribution metrics</li>
 *   <li>{@link #countByEmailVerified(Boolean)} - Email verification tracking</li>
 * </ul>
 *
 * <p>Common usage patterns:
 * <pre>
 * // Authentication
 * User user = userRepository.findByEmail("student@eng.psu.edu.eg");
 *
 * // Admin approval workflow
 * List&lt;User&gt; pending = userRepository.findByStatus(User.Status.PENDING);
 *
 * // Dashboard statistics
 * Long pendingCount = userRepository.countByStatus(User.Status.PENDING);
 * </pre>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Integer>{
    /**
     * Finds user by email address.
     * Used for authentication and email uniqueness validation.
     *
     * @param email the user's email address
     * @return the user with matching email, or null if not found
     */
    User findByEmail(String email);

    /**
     * Finds user by national ID number.
     * Alternative authentication method for users preferring national ID login.
     *
     * @param nationalId the 14-digit Egyptian national ID
     * @return the user with matching national ID, or null if not found
     */
    User findByNationalId(String nationalId);
    
    /**
     * Retrieves all users with specific status.
     * Used in admin approval workflow to get pending/approved/rejected users.
     *
     * @param status the user status to filter by (PENDING, APPROVED, REJECTED)
     * @return list of users with matching status
     */
    List<User> findByStatus(User.Status status);

    /**
     * Counts users by status for dashboard statistics.
     *
     * @param status the user status to count
     * @return number of users with given status
     */
    Long countByStatus(User.Status status);

    /**
     * Counts users by role for dashboard statistics.
     *
     * @param role the user role to count (STUDENT, ADMIN)
     * @return number of users with given role
     */
    Long countByRole(User.Role role);

    /**
     * Counts users by email verification status.
     *
     * @param emailVerified true to count verified users, false for unverified
     * @return number of users with given verification status
     */
    Long countByEmailVerified(Boolean emailVerified);
}
