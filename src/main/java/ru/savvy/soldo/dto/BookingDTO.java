package ru.savvy.soldo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.savvy.soldo.model.enums.BookingStatus;

@Data
@Builder
public class BookingDTO {

    @NotNull(message = "eventId обязателен")
    private Long eventId;

    private BookingStatus status;
}