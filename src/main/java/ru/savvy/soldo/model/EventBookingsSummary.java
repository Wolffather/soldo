package ru.savvy.soldo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_bookings_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventBookingsSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "total_bookings", nullable = false)
    private Integer totalBookings;

    @Column(name = "confirmed_bookings", nullable = false)
    private Integer confirmedBookings;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats ;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public static EventBookingsSummary of(Event event) {
        return EventBookingsSummary
                .builder()
                .event(event)
                .totalBookings(0)
                .confirmedBookings(0)
                .availableSeats(event.getNumOfParticipants())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
