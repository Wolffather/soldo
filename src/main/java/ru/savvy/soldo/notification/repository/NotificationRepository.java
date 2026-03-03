package ru.savvy.soldo.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.savvy.soldo.notification.model.Notification;
import ru.savvy.soldo.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByUserIdAndSentFalse(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.sent = false AND n.scheduledAt <= :now")
    List<Notification> findPendingScheduled(LocalDateTime now);

    List<Notification> findByEventIdAndType(Long eventId, NotificationType type);
}