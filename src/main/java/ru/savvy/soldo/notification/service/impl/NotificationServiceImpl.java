package ru.savvy.soldo.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.notification.dto.NotificationDTO;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.notification.model.Notification;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.notification.model.NotificationType;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.notification.repository.NotificationRepository;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.notification.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public void createAndSend(Long userId, Long eventId, Long bookingId,
                              NotificationType type, String message) {
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventId != null ? eventRepository.findById(eventId).orElse(null) : null;
        Booking booking = bookingId != null ? bookingRepository.findById(bookingId).orElse(null) : null;

        Notification notification = Notification.builder()
                .user(user)
                .event(event)
                .booking(booking)
                .type(type)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void schedule(Long userId, Long eventId, Long bookingId,
                         NotificationType type, String message, LocalDateTime scheduledAt) {
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventId != null ? eventRepository.findById(eventId).orElse(null) : null;
        Booking booking = bookingId != null ? bookingRepository.findById(bookingId).orElse(null) : null;

        Notification notification = Notification.builder()
                .user(user)
                .event(event)
                .booking(booking)
                .type(type)
                .message(message)
                .scheduledAt(scheduledAt)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void processScheduled() {
        // Scheduled notifications are stored for record-keeping; delivery channels TBD
    }

    @Override
    public List<NotificationDTO> getByUserId(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, null)
                .map(this::toDTO)
                .toList();
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .userId(n.getUser().getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .sent(n.getSent())
                .sentAt(n.getSentAt())
                .scheduledAt(n.getScheduledAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
