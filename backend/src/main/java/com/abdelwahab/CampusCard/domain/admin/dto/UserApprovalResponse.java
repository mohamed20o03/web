package com.abdelwahab.CampusCard.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for user approval review.
 * Contains all information an admin needs to approve/reject a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApprovalResponse {
    private Integer id;
    private String email;
    private Boolean emailVerified;
    private String nationalId;
    private String firstName;
    private String lastName;
    private java.time.LocalDate birthDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String role; // student, admin
    private Integer year;
    private String faculty;
    private String department;
    private String profilePhotoUrl;
    private String nationalIdScanUrl;
    private String registrationDate;
}
