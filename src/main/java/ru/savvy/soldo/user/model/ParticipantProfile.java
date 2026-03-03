package ru.savvy.soldo.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "participant_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "medical_notes")
    private String medicalNotes;

    @Column(name = "parent_full_name")
    private String parentFullName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "consent_personal_data")
    @Builder.Default
    private Boolean consentPersonalData = false;

    @Column(name = "consent_photo_video")
    @Builder.Default
    private Boolean consentPhotoVideo = false;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}