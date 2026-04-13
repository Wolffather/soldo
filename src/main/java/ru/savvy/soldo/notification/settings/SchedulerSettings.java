package ru.savvy.soldo.notification.settings;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduler_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerSettings {

    @Id
    private Long id = 1L;

    @Column(name = "event_reminder_cron", nullable = false)
    private String eventReminderCron;

    @Column(name = "payment_reminder_cron", nullable = false)
    private String paymentReminderCron;

    @Column(name = "event_reminder_days_before", nullable = false)
    private int eventReminderDaysBefore;

    @Column(name = "payment_reminder_days_before", nullable = false)
    private int paymentReminderDaysBefore;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
