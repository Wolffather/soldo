package ru.savvy.soldo.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDocumentResponse {

    private Long id;
    private Long bookingId;
    private String eventTitle;

    // Данные шаблона документа
    private Long templateId;
    private String templateName;
    private String templateDescription;
    private String templateFileUrl;
    private Boolean templateIsRequired;
    private String templateCategoryFormat;

    // Статус доставки / подписи
    private Boolean delivered;
    private LocalDateTime deliveredAt;
    private Boolean archived;

    // Электронная подпись
    private String signerName;
    private LocalDateTime signedAt;

    /** Данные сторон договора (JSON key-value), зафиксированные при подписании */
    private String filledData;

    /** Требует ли шаблон электронной подписи (true) или только ознакомления (false) */
    private Boolean templateRequiresSignature;

    /** Дата и время отправки письма с документами участнику (null — ещё не отправлено) */
    private LocalDateTime emailSentAt;
}
