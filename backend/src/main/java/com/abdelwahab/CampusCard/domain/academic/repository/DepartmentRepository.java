package com.abdelwahab.CampusCard.domain.academic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abdelwahab.CampusCard.domain.academic.model.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByFacultyId(Integer facultyId);
    Department findByNameAndFacultyId(String name, Integer facultyId);
}
