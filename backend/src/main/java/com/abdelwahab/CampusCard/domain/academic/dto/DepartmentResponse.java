package com.abdelwahab.CampusCard.domain.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer facultyId;
}

