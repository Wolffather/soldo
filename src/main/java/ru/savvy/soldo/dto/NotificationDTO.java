package ru.savvy.soldo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String message;
    private Boolean sent;
    private LocalDateTime sentAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}