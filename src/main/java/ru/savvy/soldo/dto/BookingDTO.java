package ru.savvy.soldo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID мероприятия обязателен")
    private Long eventId;

    @NotNull(message = "Дата создания обязательна")
    @PastOrPresent(message = "Дата создания не может быть в будущем")
    private LocalDateTime createdAt;
}
