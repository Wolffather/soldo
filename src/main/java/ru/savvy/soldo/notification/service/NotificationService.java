package ru.savvy.soldo.notification.service;

import ru.savvy.soldo.notification.dto.NotificationDTO;
import ru.savvy.soldo.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

    void createAndSend(Long userId, Long eventId, Long bookingId,
                       NotificationType type, String message);

    void schedule(Long userId, Long eventId, Long bookingId,
                  NotificationType type, String message, LocalDateTime scheduledAt);

    void processScheduled();

    List<NotificationDTO> getByUserId(Long userId);
}