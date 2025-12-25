package com.abdelwahab.CampusCard.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration responses.
 * Returned after successful or failed signup attempt.
 *
 * <p>Response fields:
 * <ul>
 *   <li><strong>id:</strong> Generated user ID (null on error)</li>
 *   <li><strong>email:</strong> Registered email address</li>
 *   <li><strong>status:</strong> Operation status ("SUCCESS" or "ERROR")</li>
 *   <li><strong>message:</strong> Human-readable result message</li>
 * </ul>
 *
 * <p>Success response example:
 * <pre>
 * {
 *   "id": 123,
 *   "email": "student@eng.psu.edu.eg",
 *   "status": "SUCCESS",
 *   "message": "Registration successful. Please check your email for verification."
 * }
 * </pre>
 *
 * <p>Error response example:
 * <pre>
 * {
 *   "id": null,
 *   "email": "student@eng.psu.edu.eg",
 *   "status": "ERROR",
 *   "message": "Email already registered"
 * }
 * </pre>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private Integer id;
    private String email;
    private String status;
    private String message;
}
