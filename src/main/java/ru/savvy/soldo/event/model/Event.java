package ru.savvy.soldo.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.season.model.Season;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventStatus status = EventStatus.PUBLISHED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Season this event belongs to. Required when category format is
     * SESSION_OUTDOOR or SESSION_CITY; null for regular events.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
