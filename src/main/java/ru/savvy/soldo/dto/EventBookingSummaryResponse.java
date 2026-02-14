package ru.savvy.soldo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventBookingSummaryResponse {
    private Long eventId;
    private String eventTitle;
    private Integer totalBookings;
    private Integer confirmedBookings;
    private Integer availableSeats;
    private LocalDateTime lastUpdated;
}
