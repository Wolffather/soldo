package ru.savvy.soldo.dto;

import lombok.Builder;
import lombok.Data;
import ru.savvy.soldo.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private String eventTitle;
    private BookingStatus status;
    private LocalDateTime createdAt;
}