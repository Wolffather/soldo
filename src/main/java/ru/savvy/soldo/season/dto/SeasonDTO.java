package ru.savvy.soldo.season.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.savvy.soldo.event.dto.EventDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonDTO {

    private Long id;

    @NotBlank(message = "Название обязательно")
    private String title;

    @NotNull(message = "Год обязателен")
    @Min(value = 2020, message = "Год не может быть меньше 2020")
    private Integer year;

    private String description;

    private String baseName;

    private String baseWebsite;

    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    /** Site section where this season is displayed: WINTER, SUMMER, ALL_YEAR. */
    private String period;

    private String status;

    private String createdAt;

    /** Populated in response: events of type SESSION_OUTDOOR/SESSION_CITY linked to this season. */
    private List<EventDTO> sessions;
}
