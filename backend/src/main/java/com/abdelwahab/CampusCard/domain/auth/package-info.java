/**
 * Authentication and user registration domain.
 * Handles user signup, login, email verification, and JWT token management.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Controllers:</strong> LoginController, SignUpController</li>
 *   <li><strong>Services:</strong> LoginService, SignUpService, EmailService</li>
 *   <li><strong>DTOs:</strong> Login/Signup requests and responses, email verification</li>
 * </ul>
 *
 * <p>Authentication flow:
 * <ol>
 *   <li>User registers via SignUpController with personal and academic info</li>
 *   <li>Account created with PENDING status</li>
 *   <li>Email verification link sent to user</li>
 *   <li>Admin reviews and approves after verification</li>
 *   <li>User logs in via LoginController to receive JWT token</li>
 *   <li>Token used for subsequent authenticated requests</li>
 * </ol>
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.auth;
