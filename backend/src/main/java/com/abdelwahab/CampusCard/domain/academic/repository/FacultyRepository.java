package com.abdelwahab.CampusCard.domain.academic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abdelwahab.CampusCard.domain.academic.model.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    Faculty findByName(String name);
}