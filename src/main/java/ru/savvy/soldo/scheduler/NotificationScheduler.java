package ru.savvy.soldo.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.enums.BookingStatus;
import ru.savvy.soldo.model.enums.NotificationType;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.NotificationService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60_000)
    public void processScheduledNotifications() {
        notificationService.processScheduled();
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void sendEventReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Event> events = eventRepository.findByDateRange(tomorrow, tomorrow);

        for (Event event : events) {
            List<Booking> bookings = bookingRepository.findByEventIdAndStatus(
                    event.getId(), BookingStatus.CONFIRMED);

            for (Booking booking : bookings) {
                String message = String.format(
                        "📅 Напоминание: завтра состоится <b>%s</b>!\nДата: %s",
                        event.getTitle(), event.getStartDate());

                notificationService.createAndSend(
                        booking.getUser().getId(),
                        event.getId(),
                        booking.getId(),
                        NotificationType.EVENT_REMINDER_1DAY,
                        message);
            }
        }

        log.info("Отправлены напоминания о {} событиях на завтра", events.size());
    }

    // Каждый день в 9:00 — напоминание об оплате
    @Scheduled(cron = "0 0 9 * * *")
    public void sendPaymentReminders() {
        LocalDate deadlineSoon = LocalDate.now().plusDays(3);
        List<Booking> unpaid = bookingRepository.findUnpaidWithDeadlineBefore(deadlineSoon);

        for (Booking booking : unpaid) {
            String message = String.format(
                    "💰 Напоминание об оплате!\nСобытие: <b>%s</b>\n" +
                            "К оплате: %s ₽\nСрок оплаты: %s",
                    booking.getEvent().getTitle(),
                    booking.getAmountDue(),
                    booking.getPaymentDeadline());

            notificationService.createAndSend(
                    booking.getUser().getId(),
                    booking.getEvent().getId(),
                    booking.getId(),
                    NotificationType.PAYMENT_REMINDER,
                    message);
        }

        log.info("Отправлены напоминания об оплате: {} бронирований", unpaid.size());
    }
}