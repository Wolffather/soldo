package ru.savvy.soldo.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingStatus;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.notification.service.NotificationService;
import ru.savvy.soldo.notification.service.TelegramSenderService;
import ru.savvy.soldo.notification.settings.SchedulerSettings;
import ru.savvy.soldo.notification.settings.SchedulerSettingsService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler implements SchedulingConfigurer {

    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final TelegramSenderService telegramSender;
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
        SchedulerSettings settings = settingsService.getSettings();
        int daysBefore = settings.getEventReminderDaysBefore();
        LocalDate targetDate = LocalDate.now().plusDays(daysBefore);
        List<Event> events = eventRepository.findByDateRange(targetDate, targetDate);

        int sent = 0;
        for (Event event : events) {
            List<Booking> bookings = bookingRepository.findByEventIdAndStatus(
                    event.getId(), BookingStatus.CONFIRMED);

            for (Booking booking : bookings) {
                if (booking.getTelegramChatId() == null) continue;

                String message = String.format(
                        "📅 Напоминание: через %d %s состоится <b>%s</b>!\nДата: %s",
                        daysBefore,
                        pluralDays(daysBefore),
                        event.getTitle(),
                        event.getStartDate());

                try {
                    telegramSender.sendMessage(booking.getTenantId(), booking.getTelegramChatId(), message);
                    sent++;
                } catch (Exception e) {
                    log.warn("Не удалось отправить напоминание о событии для бронирования {}: {}",
                            booking.getId(), e.getMessage());
                }
            }
        }

        log.info("Напоминания о событиях: обработано {} событий, отправлено {} сообщений (за {} дней)",
                events.size(), sent, daysBefore);
    }

    public void sendPaymentReminders() {
        SchedulerSettings settings = settingsService.getSettings();
        int daysBefore = settings.getPaymentReminderDaysBefore();
        LocalDate deadlineSoon = LocalDate.now().plusDays(daysBefore);
        List<Booking> unpaid = bookingRepository.findUnpaidWithDeadlineBefore(deadlineSoon);

        int sent = 0;
        for (Booking booking : unpaid) {
            if (booking.getTelegramChatId() == null) continue;

            String message = String.format(
                    "💰 Напоминание об оплате!\nСобытие: <b>%s</b>\n" +
                            "К оплате: %s ₽\nСрок оплаты: %s",
                    booking.getEvent().getTitle(),
                    booking.getAmountDue(),
                    booking.getPaymentDeadline());

            try {
                telegramSender.sendMessage(booking.getTelegramChatId(), message);
                sent++;
            } catch (Exception e) {
                log.warn("Не удалось отправить напоминание об оплате для бронирования {}: {}",
                        booking.getId(), e.getMessage());
            }
        }

        log.info("Напоминания об оплате: обработано {} бронирований, отправлено {} сообщений",
                unpaid.size(), sent);
    }

    private static String pluralDays(int days) {
        if (days == 1) return "день";
        if (days >= 2 && days <= 4) return "дня";
        return "дней";
    }
}
