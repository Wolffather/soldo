package ru.savvy.soldo.tenant.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tenant_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantConfig {

    @Id
    private Long tenantId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "event_label", nullable = false)
    @Builder.Default
    private String eventLabel = "Событие";

    @Column(name = "participant_label", nullable = false)
    @Builder.Default
    private String participantLabel = "Участник";

    @Column(name = "booking_label", nullable = false)
    @Builder.Default
    private String bookingLabel = "Бронирование";

    @Column(name = "booking_rules", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private String bookingRules = "{}";

    @Column(name = "profile_fields", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private String profileFields = "[]";

    @Column(name = "notifications_config", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private String notificationsConfig = "{}";

    @Column(name = "branding", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private String branding = "{}";

    // ─── Telegram Bot ──────────────────────────────────────────────────────
    /** Токен бота, полученный от @BotFather. Если null — бот не подключён. */
    @Column(name = "telegram_bot_token")
    private String telegramBotToken;

    /** Username бота (без @) — используется для формирования ссылки t.me/{username}. */
    @Column(name = "telegram_bot_username")
    private String telegramBotUsername;

    /** Секрет для проверки подлинности webhook-запросов от Telegram. */
    @Column(name = "telegram_webhook_secret")
    private String telegramWebhookSecret;
}

