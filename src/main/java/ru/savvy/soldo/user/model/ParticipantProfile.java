package ru.savvy.soldo.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "participant_profiles")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
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

    // ── Паспортные данные родителя ──────────────────────────────────────────

    @Column(name = "parent_birth_date")
    private LocalDate parentBirthDate;

    @Column(name = "parent_passport_series", length = 10)
    private String parentPassportSeries;

    @Column(name = "parent_passport_number", length = 20)
    private String parentPassportNumber;

    @Column(name = "parent_passport_issued_by", columnDefinition = "TEXT")
    private String parentPassportIssuedBy;

    @Column(name = "parent_passport_issued_date")
    private LocalDate parentPassportIssuedDate;

    @Column(name = "registration_address", columnDefinition = "TEXT")
    private String registrationAddress;

    // ── Документ ребёнка ───────────────────────────────────────────────────

    /** BIRTH_CERTIFICATE или PASSPORT */
    @Column(name = "child_document_type", length = 50)
    private String childDocumentType;

    @Column(name = "child_document_series", length = 10)
    private String childDocumentSeries;

    @Column(name = "child_document_number", length = 20)
    private String childDocumentNumber;

    @Column(name = "consent_personal_data")
    @Builder.Default
    private Boolean consentPersonalData = false;

    @Column(name = "consent_photo_video")
    @Builder.Default
    private Boolean consentPhotoVideo = false;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

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