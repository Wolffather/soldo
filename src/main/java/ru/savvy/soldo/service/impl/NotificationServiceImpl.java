package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.NotificationDTO;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.Notification;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.model.UserAuthProvider;
import ru.savvy.soldo.model.enums.AuthProviderType;
import ru.savvy.soldo.model.enums.NotificationType;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.repository.NotificationRepository;
import ru.savvy.soldo.repository.UserAuthProviderRepository;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.NotificationService;
import ru.savvy.soldo.service.TelegramSenderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final UserAuthProviderRepository authProviderRepository;
    private final TelegramSenderService telegramSender;

    /** Returns the Telegram chat ID for a user (via user_auth_providers), or null if none. */
    private Long getTelegramChatId(User user) {
        if (user == null) return null;
        return authProviderRepository.findByUserId(user.getId()).stream()
                .filter(p -> p.getProvider() == AuthProviderType.TELEGRAM)
                .map(UserAuthProvider::getProviderUserId)
                .map(id -> {
                    try { return Long.parseLong(id); } catch (NumberFormatException e) { return null; }
                })
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

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

        // Отправляем в Telegram (если у пользователя привязан Telegram через провайдер)
        Long telegramId = getTelegramChatId(user);
        if (telegramId != null) {
            try {
                telegramSender.sendMessage(telegramId, message);
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Ошибка отправки уведомления пользователю {}: {}",
                          userId, e.getMessage());
            }
        }
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
        List<Notification> pending = notificationRepository.findPendingScheduled(LocalDateTime.now());

        for (Notification notification : pending) {
            User user = notification.getUser();
            Long telegramId = getTelegramChatId(user);
            if (telegramId != null) {
                try {
                    telegramSender.sendMessage(telegramId, notification.getMessage());
                    notification.setSent(true);
                    notification.setSentAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    log.info("Отправлено уведомление {} пользователю {}",
                             notification.getType(), user.getId());
                } catch (Exception e) {
                    log.error("Ошибка отправки уведомления {}: {}",
                              notification.getId(), e.getMessage());
                }
            }
        }
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
