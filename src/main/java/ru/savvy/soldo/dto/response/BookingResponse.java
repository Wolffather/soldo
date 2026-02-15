package ru.savvy.soldo.dto.response;

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
    private String status;
    private String paymentStatus;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private LocalDate paymentDeadline;
    private String createdAt;
}