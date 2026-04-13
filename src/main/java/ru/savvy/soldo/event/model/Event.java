package ru.savvy.soldo.event.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import ru.savvy.soldo.event.model.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EventCategory category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "game_master")
    private String gameMaster;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Reduced price when the participant has a ПФДО certificate (государственный сертификат
     * персонифицированного финансирования дополнительного образования).
     * Applicable only for SESSION_OUTDOOR events. Null means not applicable.
     */
    @Column(name = "price_with_certificate", precision = 10, scale = 2)
    private BigDecimal priceWithCertificate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventStatus status = EventStatus.PUBLISHED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
