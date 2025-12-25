package com.abdelwahab.CampusCard.domain.profile.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.common.converter.VisibilityConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a user's public profile information.
 * One-to-one relationship with User entity - each user has exactly one profile.
 *
 * <p>Profile contains:
 * <ul>
 *   <li><strong>Profile Photo:</strong> URL to MinIO-stored photo</li>
 *   <li><strong>Bio:</strong> User's self-description (TEXT field, moderation checked)</li>
 *   <li><strong>Contact:</strong> Phone number (optional)</li>
 *   <li><strong>Social Links:</strong> LinkedIn and GitHub URLs</li>
 *   <li><strong>Interests:</strong> Academic/professional interests (TEXT field)</li>
 *   <li><strong>Visibility:</strong> Who can view this profile</li>
 * </ul>
 *
 * <p>Visibility levels:
 * <ul>
 *   <li><strong>PUBLIC:</strong> Visible in student directory to all authenticated users</li>
 *   <li><strong>STUDENTS_ONLY:</strong> Only visible to other approved students</li>
 *   <li><strong>PRIVATE:</strong> Hidden from directory, only accessible by profile owner</li>
 * </ul>
 *
 * <p>Profile lifecycle:
 * <ol>
 *   <li>Created automatically when user registers (empty profile)</li>
 *   <li>User completes profile after approval (photo, bio, links)</li>
 *   <li>Profile visible based on visibility setting and user approval status</li>
 *   <li>Deleted when user account is deleted (cascade)</li>
 * </ol>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "profiles")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional = false) // optinal = false is equivalent to NOT NULL
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        unique = true,
        nullable = false
    )
    private User user;

    @Column(name = "profile_photo", length = 255)
    private String profilePhoto;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String linkedin;

    @Column(length = 255)
    private String github;

    @Column(columnDefinition = "TEXT")
    private String interests;

    @Column(nullable = false)
    @Builder.Default
    @Convert(converter = VisibilityConverter.class)
    private Visibility visibility = Visibility.PUBLIC;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    
    public enum Visibility {
        PUBLIC("public"),
        STUDENTS_ONLY("students_only"),
        PRIVATE("private");

        private final String value;

        Visibility(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
        
}
