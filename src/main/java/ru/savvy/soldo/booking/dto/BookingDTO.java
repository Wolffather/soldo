package ru.savvy.soldo.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.savvy.soldo.booking.model.BookingStatus;

@Data
@Builder
public class BookingDTO {

    @NotNull(message = "eventId обязателен")
    private Long eventId;

    private BookingStatus status;
}