package ru.savvy.soldo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.savvy.soldo.annotation.ValidDateOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDateOrder
public class EventDTO {

    private Long id;

    @NotBlank(message = "Название обязательно")
    private String title;

    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDate startDate;

    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDate endDate;

    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @Positive(message = "Количество участников должно быть положительным")
    private Integer maxParticipants;

    private Long categoryId;
    private String categoryName;

    private String description;
    private String gameMaster;

    private String status;
    private String createdAt;

    /**
     * ID of the Season this event belongs to.
     * Required for events of type SESSION_OUTDOOR or SESSION_CITY.
     */
    private Long seasonId;

    /**
     * Read-only: title of the linked season (for display purposes).
     */
    private String seasonTitle;

    /**
     * Number of available spots.
     * Computed from EventBookingsSummary.numOfParticipants.
     */
    private Integer availableSpots;
}
