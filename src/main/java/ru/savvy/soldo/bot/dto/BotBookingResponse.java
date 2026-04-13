package ru.savvy.soldo.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotBookingResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String paymentStatus;
    private BigDecimal amountDue;
}
