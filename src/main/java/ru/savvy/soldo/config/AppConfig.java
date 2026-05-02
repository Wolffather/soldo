package ru.savvy.soldo.config;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppConfig {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String domain;

    @Column(name = "event_label", nullable = false)
    @Builder.Default
    private String eventLabel = "Событие";

    @Column(name = "participant_label", nullable = false)
    @Builder.Default
    private String participantLabel = "Участник";

    @Column(name = "booking_label", nullable = false)
    @Builder.Default
    private String bookingLabel = "Бронирование";
}
