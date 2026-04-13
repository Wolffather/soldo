package ru.savvy.soldo.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TelegramBotSetupRequest {
    /** Токен бота, полученный от @BotFather. */
    @NotBlank(message = "Токен бота обязателен")
    private String botToken;
}
