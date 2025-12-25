package com.abdelwahab.CampusCard.domain.profile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abdelwahab.CampusCard.domain.academic.dto.DepartmentResponse;
import com.abdelwahab.CampusCard.domain.academic.dto.FacultyResponse;
import com.abdelwahab.CampusCard.domain.profile.service.PublicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    
    private final PublicService publicService;

    /**
     * GET /api/public/faculties - Get all faculties
     * Public endpoint - no authentication required
     */
    @GetMapping("/faculties")
    public ResponseEntity<List<FacultyResponse>> getAllFaculties() {
        List<FacultyResponse> faculties = publicService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    /**
     * GET /api/public/departments?facultyId={id} - Get departments for a faculty
     * Public endpoint - no authentication required
     */
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByFaculty(
            @RequestParam(required = false) Integer facultyId) {
        List<DepartmentResponse> departments;
        if (facultyId != null) {
            departments = publicService.getDepartmentsByFaculty(facultyId);
        } else {
            departments = publicService.getAllDepartments();
        }
        return ResponseEntity.ok(departments);
    }
}

