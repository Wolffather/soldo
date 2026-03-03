package ru.savvy.soldo.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingSummaryResponse {
    private Long eventId;
    private String eventTitle;
    private Long totalBookings;
    private Long confirmedBookings;
    private Long pendingBookings;
    private Long cancelledBookings;
    private Long availableSeats;
}