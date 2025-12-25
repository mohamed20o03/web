package com.abdelwahab.CampusCard.domain.admin.dto;

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
public class ChangeRoleRequest {
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "STUDENT|ADMIN|student|admin", message = "Role must be STUDENT or ADMIN")
    private String role;
}

