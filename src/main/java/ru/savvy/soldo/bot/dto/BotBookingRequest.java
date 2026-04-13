package ru.savvy.soldo.bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BotBookingRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private Long telegramId;

    @NotBlank
    private String guestName;

    private String guestPhone;

    private String guestEmail;

    @NotBlank
    private String tenantSlug;
}
