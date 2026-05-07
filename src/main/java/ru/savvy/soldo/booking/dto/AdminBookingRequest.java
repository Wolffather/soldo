package ru.savvy.soldo.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminBookingRequest {

    @NotNull(message = "Необходимо указать мероприятие")
    private Long eventId;

    private String guestName;
    private String guestPhone;
    private String guestEmail;

    /** Есть ли сертификат ПФДО */
    private boolean hasCertificate;

    /** Примечание администратора */
    private String notes;

    /** ID выбранной опции оплаты (если у события несколько вариантов) */
    private Long priceOptionId;
}
