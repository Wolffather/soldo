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

    @Column(name = "num_of_participants", nullable = false)
    private Integer numOfParticipants ;

    @Column(name = "pending_bookings", nullable = false)
    private Long pendingBookings;

    @Column(name = "cancelled_bookings", nullable = false)
    private Long cancelledBookings;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public static EventBookingsSummary of(Event event) {
        return EventBookingsSummary
                .builder()
                .event(event)
                .totalBookings(0)
                .confirmedBookings(0)
                .pendingBookings(0L)
                .cancelledBookings(0L)
                // Parent camp events have no maxParticipants — default to 0
                .numOfParticipants(event.getMaxParticipants() != null ? event.getMaxParticipants() : 0)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
