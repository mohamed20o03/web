package com.abdelwahab.CampusCard.domain.auth.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.abdelwahab.CampusCard.domain.common.validation.annotation.PsuEmail;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class SignUpRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email is required")
    @PsuEmail(message = "Email must end with @eng.psu.edu.eg")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
    
    @NotBlank(message = "National ID is required")
    @Size(max = 50, message = "National ID must not exceed 50 characters")
    @Pattern(
        regexp = "^[0-9]{14}$",
        message = "National ID must be exactly 14 digits"
    )
    private String nationalId;
    
    @NotNull(message = "National ID scan is required")
    private MultipartFile nationalIdScan;
   
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    private Integer year;
    
    @NotNull(message = "Faculty is required")
    private Integer facultyId;
    
    @NotNull(message = "Department is required")
    private Integer departmentId;
}
