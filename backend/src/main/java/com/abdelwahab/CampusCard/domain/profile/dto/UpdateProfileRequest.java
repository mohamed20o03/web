package com.abdelwahab.CampusCard.domain.profile.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    // --- User fields ---
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^$|^\\d{14}$", message = "National ID must be exactly 14 digits")
    private String nationalId;

    // For updating national ID scan (URL or file name, actual upload handled separately)
    private String nationalIdScan;

    private Integer facultyId;
    private Integer departmentId;

    private Integer year;

    // --- Profile fields ---
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Pattern(regexp = "^$|^[+]?[0-9]{10,20}$", message = "Phone number must be between 10 and 20 digits")
    private String phone;

    @Pattern(regexp = "^$|^https?://(www\\.)?linkedin\\.com/.*$", message = "Invalid LinkedIn URL")
    private String linkedin;

    @Pattern(regexp = "^$|^https?://(www\\.)?github\\.com/.*$", message = "Invalid GitHub URL")
    private String github;

    @Size(max = 500, message = "Interests must not exceed 500 characters")
    private String interests;

    @Pattern(regexp = "^(PUBLIC|STUDENTS_ONLY|PRIVATE)?$", message = "Visibility must be PUBLIC, STUDENTS_ONLY, or PRIVATE")
    private String visibility;
}
