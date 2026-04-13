package ru.savvy.soldo.notification.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SchedulerSettingsDTO(
        @NotBlank String eventReminderCron,
        @NotBlank String paymentReminderCron,
        @Min(1) int eventReminderDaysBefore,
        @Min(1) int paymentReminderDaysBefore
) {}
