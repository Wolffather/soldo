package ru.savvy.soldo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Тип мероприятия обязателен")
    private String type;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate endDate;

    @NotNull(message = "Цена обязательна")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Double price;

    @NotNull(message = "Количество участников обязательно")
    @Min(value = 1, message = "Должен быть хотя бы 1 участник")
    private Integer numOfParticipants;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;
}


