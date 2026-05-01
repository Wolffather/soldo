package ru.savvy.soldo.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.savvy.soldo.notification.service.NotificationService;
import ru.savvy.soldo.notification.settings.SchedulerSettingsService;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler implements SchedulingConfigurer {

    private final NotificationService notificationService;
    private final SchedulerSettingsService settingsService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(
                this::sendEventReminders,
                ctx -> new CronTrigger(settingsService.getSettings().getEventReminderCron())
                        .nextExecution(ctx)
        );

        registrar.addTriggerTask(
                this::sendPaymentReminders,
                ctx -> new CronTrigger(settingsService.getSettings().getPaymentReminderCron())
                        .nextExecution(ctx)
        );

        registrar.addFixedRateTask(this::processScheduledNotifications, Duration.ofSeconds(60));
    }

    public void processScheduledNotifications() {
        notificationService.processScheduled();
    }

    public void sendEventReminders() {
        log.info("Event reminders: delivery channel not configured");
    }

    public void sendPaymentReminders() {
        log.info("Payment reminders: delivery channel not configured");
    }
}
