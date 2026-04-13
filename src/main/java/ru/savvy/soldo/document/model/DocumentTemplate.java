package ru.savvy.soldo.document.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_templates")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "category_format", nullable = false)
    private String categoryFormat;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = true;

    /** Если true — документ требует электронной подписи.
     *  Если false — достаточно подтверждения ознакомления (без ФИО). */
    @Column(name = "requires_signature")
    @Builder.Default
    private Boolean requiresSignature = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
