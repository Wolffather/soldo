package ru.savvy.soldo.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    private Long id;
    private String slug;
    private String name;
    private String domain;
    private String status;

    // Subscription
    private String plan;
    private Integer maxEvents;
    private Integer maxBookingsPerMonth;
    private Integer maxAdminUsers;
    private boolean customDomainEnabled;
    private boolean apiAccessEnabled;

    // Config labels
    private String eventLabel;
    private String participantLabel;
    private String bookingLabel;

    // Telegram Bot
    private boolean telegramBotEnabled;
    private String telegramBotUsername;
    private String telegramWebhookUrl;
}
