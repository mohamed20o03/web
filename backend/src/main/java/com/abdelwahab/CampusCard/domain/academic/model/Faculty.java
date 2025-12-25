package com.abdelwahab.CampusCard.domain.academic.model;

import com.abdelwahab.CampusCard.domain.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="faculties")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable=false, length=100)
    private String name;

    @Column(length=500)
    private String description;

    @Column(name="years_numbers", nullable=false)
    private Integer yearsNumbers;

    @OneToMany(mappedBy="faculty")
    private List<Department> departments;

    @OneToMany(mappedBy="faculty")
    private List<User> users;
}