package com.abdelwahab.CampusCard.domain.moderation.model;

import com.abdelwahab.CampusCard.domain.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="flagged_content")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlaggedContent {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(columnDefinition="TEXT", nullable=false)
    private String content;

    @CreationTimestamp
    @Column(name="flagged_at", updatable=false)
    private LocalDateTime flaggedAt;
}
