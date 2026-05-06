package ru.savvy.soldo.event.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.savvy.soldo.shared.annotation.ValidDateOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    private String description;

    private String status;
    private String createdAt;

    /**
     * Number of available spots.
     * Computed from EventBookingsSummary.numOfParticipants.
     */
    private Integer availableSpots;

    /** Опции оплаты (не маппятся автоматически — загружаются отдельно в сервисе) */
    private List<EventPriceOptionDTO> priceOptions;
}
