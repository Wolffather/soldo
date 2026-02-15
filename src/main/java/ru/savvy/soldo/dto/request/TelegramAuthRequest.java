package ru.savvy.soldo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TelegramAuthRequest {

    @NotNull(message = "telegramId обязателен")
    private Long telegramId;

    @NotBlank(message = "firstName обязателен")
    private String firstName;

    private String lastName;
    private String username;
}