package ru.savvy.soldo.content.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 300)
    private String role;

    /** Краткие инициалы для аватара (авто-вычисляется если не задано) */
    @Column(length = 10)
    private String initials;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String quote;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.initials == null || this.initials.isBlank()) {
            this.initials = computeInitials(this.name);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.initials == null || this.initials.isBlank()) {
            this.initials = computeInitials(this.name);
        }
    }

    private static String computeInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(p.charAt(0));
            if (sb.length() >= 2) break;
        }
        return sb.toString().toUpperCase();
    }
}
