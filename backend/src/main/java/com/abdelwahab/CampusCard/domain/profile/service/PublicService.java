package com.abdelwahab.CampusCard.domain.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.abdelwahab.CampusCard.domain.academic.dto.DepartmentResponse;
import com.abdelwahab.CampusCard.domain.academic.dto.FacultyResponse;
import com.abdelwahab.CampusCard.domain.academic.model.Department;
import com.abdelwahab.CampusCard.domain.academic.model.Faculty;
import com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository;
import com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicService {
    
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Get all faculties
     */
    public List<FacultyResponse> getAllFaculties() {
        List<Faculty> faculties = facultyRepository.findAll();
        return faculties.stream()
                .map(this::mapToFacultyResponse)
                .toList();
    }

    /**
     * Get all departments
     */
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(this::mapToDepartmentResponse)
                .toList();
    }

    /**
     * Get departments by faculty ID
     */
    public List<DepartmentResponse> getDepartmentsByFaculty(Integer facultyId) {
        List<Department> departments = departmentRepository.findByFacultyId(facultyId);
        return departments.stream()
                .map(this::mapToDepartmentResponse)
                .toList();
    }

    private FacultyResponse mapToFacultyResponse(Faculty faculty) {
        return FacultyResponse.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .description(faculty.getDescription())
                .yearsNumbers(faculty.getYearsNumbers())
                .build();
    }

    private DepartmentResponse mapToDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .facultyId(department.getFaculty().getId())
                .build();
    }
}

