package ru.savvy.soldo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;

    @NotBlank(message = "Укажите ваше имя")
    private String authorName;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String text;

    @NotNull(message = "Укажите оценку")
    @Min(value = 1, message = "Минимальная оценка — 1")
    @Max(value = 5, message = "Максимальная оценка — 5")
    private Integer rating;

    private Boolean approved;
    private LocalDateTime createdAt;
}
