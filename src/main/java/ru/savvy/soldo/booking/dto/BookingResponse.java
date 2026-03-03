package ru.savvy.soldo.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long eventId;
    private String eventTitle;
    /** Формат категории события: CAMP_CITY, CAMP_OUTDOOR, TRIP, RECURRING, ONE_TIME */
    private String categoryFormat;
    private String status;
    private String paymentStatus;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private LocalDate paymentDeadline;
    private String createdAt;
}
