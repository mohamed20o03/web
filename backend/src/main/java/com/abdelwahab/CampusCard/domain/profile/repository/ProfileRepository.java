package com.abdelwahab.CampusCard.domain.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abdelwahab.CampusCard.domain.profile.model.Profile;
import com.abdelwahab.CampusCard.domain.profile.model.Profile.Visibility;
import com.abdelwahab.CampusCard.domain.user.model.User;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUserId(Integer userId);

    @Query("""
        SELECT p
        FROM Profile p
        WHERE p.user.status = :status
          AND p.user.role = :role
          AND p.visibility = :visibility
    """)
    List<Profile> findPublicApprovedStudents(
            User.Status status,
            User.Role role,
            Visibility visibility
    );
}
