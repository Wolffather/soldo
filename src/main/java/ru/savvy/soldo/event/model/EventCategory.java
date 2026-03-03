package ru.savvy.soldo.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.savvy.soldo.event.model.EventFormat;
import ru.savvy.soldo.season.model.SeasonType;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventFormat format;

    @Enumerated(EnumType.STRING)
    private SeasonType season;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}