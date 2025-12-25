package com.abdelwahab.CampusCard.domain.academic.model;

import com.abdelwahab.CampusCard.domain.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="departments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(length=500)
    private String description;

    @ManyToOne(optional=false)
    @JoinColumn(name="faculty_id", nullable=false)
    private Faculty faculty;

    @OneToMany(mappedBy="department")
    private List<User> users;
}
