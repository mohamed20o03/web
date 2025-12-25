package com.abdelwahab.CampusCard.domain.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVisibilityRequest {
    
    @NotBlank(message = "Visibility is required")
    @Pattern(regexp = "PUBLIC|STUDENTS_ONLY|PRIVATE|public|students_only|private", message = "Visibility must be PUBLIC, STUDENTS_ONLY, or PRIVATE")
    private String visibility;
}
