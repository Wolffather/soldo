package ru.savvy.soldo.model;

import jakarta.persistence.*;
import lombok.*;
import ru.savvy.soldo.model.enums.EventStatus;
import ru.savvy.soldo.model.enums.SeasonType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seasons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer year;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_name")
    private String baseName;

    @Column(name = "base_website")
    private String baseWebsite;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Which site section this season appears on: WINTER, SUMMER, ALL_YEAR.
     * Unlike EventCategory.format, this is not an event type — it just
     * determines where on the public site the season is displayed.
     */
    @Enumerated(EnumType.STRING)
    private SeasonType period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.PUBLISHED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
