package com.abdelwahab.CampusCard.domain.moderation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="banned_words")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannedWord {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable=false, length=100)
    private String word;

    @CreationTimestamp
    @Column(name="added_at", updatable=false)
    private LocalDateTime addedAt;
}
