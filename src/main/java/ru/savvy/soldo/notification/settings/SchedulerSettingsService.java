package ru.savvy.soldo.notification.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulerSettingsService {

    private final SchedulerSettingsRepository repository;

    public SchedulerSettings getSettings() {
        return repository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Настройки планировщика не найдены в БД"));
    }

    public SchedulerSettingsDTO getSettingsDto() {
        SchedulerSettings s = getSettings();
        return new SchedulerSettingsDTO(
                s.getEventReminderCron(),
                s.getPaymentReminderCron(),
                s.getEventReminderDaysBefore(),
                s.getPaymentReminderDaysBefore()
        );
    }

    @Transactional
    public SchedulerSettingsDTO updateSettings(SchedulerSettingsDTO dto) {
        if (!CronExpression.isValidExpression(dto.eventReminderCron())) {
            throw new IllegalArgumentException("Некорректное cron-выражение для напоминаний о событиях: " + dto.eventReminderCron());
        }
        if (!CronExpression.isValidExpression(dto.paymentReminderCron())) {
            throw new IllegalArgumentException("Некорректное cron-выражение для напоминаний об оплате: " + dto.paymentReminderCron());
        }

        SchedulerSettings settings = getSettings();
        settings.setEventReminderCron(dto.eventReminderCron());
        settings.setPaymentReminderCron(dto.paymentReminderCron());
        settings.setEventReminderDaysBefore(dto.eventReminderDaysBefore());
        settings.setPaymentReminderDaysBefore(dto.paymentReminderDaysBefore());
        repository.save(settings);

        return new SchedulerSettingsDTO(
                settings.getEventReminderCron(),
                settings.getPaymentReminderCron(),
                settings.getEventReminderDaysBefore(),
                settings.getPaymentReminderDaysBefore()
        );
    }
}
