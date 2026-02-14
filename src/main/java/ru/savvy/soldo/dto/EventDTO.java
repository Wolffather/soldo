package ru.savvy.soldo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.savvy.soldo.annotation.ValidDateOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@ValidDateOrder
public class EventDTO {

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Тип обязателен")
    private String type;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate endDate;

    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @Positive(message = "Количество участников должно быть положительным")
    private Integer numOfParticipants;

    private String description;
}