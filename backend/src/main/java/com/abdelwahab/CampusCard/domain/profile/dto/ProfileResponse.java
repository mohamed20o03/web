package com.abdelwahab.CampusCard.domain.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Integer id;
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private java.time.LocalDate birthDate;
    private String profilePhoto;
    private String bio;
    private String phone;
    private String linkedin;
    private String github;
    private String interests;
    private String visibility;
    private Integer year;
    private String faculty;
    private String department;
    private String role;
    private String status; // ADDED: pending, approved, rejected
    private String rejectionReason; // ADDED: reason if rejected
    private String nationalIdScan; // ADDED: expose scan URL for admin/frontend
}
