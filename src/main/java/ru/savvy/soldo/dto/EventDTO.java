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

    private Long id;

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate startDate;

    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate endDate;

    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @NotNull(message = "Количество участников обязательно")
    @Positive(message = "Количество участников должно быть положительным")
    private Integer maxParticipants;

    private Long categoryId;
    private String categoryName;

    private String description;
    private String gameMaster;

    private String status;
    private String createdAt;
}