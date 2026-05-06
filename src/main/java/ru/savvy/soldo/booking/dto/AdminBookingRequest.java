package ru.savvy.soldo.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.savvy.soldo.booking.model.BookingStatus;

@Data
public class AdminBookingRequest {

    @NotNull(message = "Необходимо указать мероприятие")
    private Long eventId;

    /** Имя гостя (если бронирует без аккаунта) */
    private String guestName;

    private String guestPhone;

    private String guestEmail;

    /** Есть ли сертификат ПФДО */
    private boolean hasCertificate;

    /**
     * Начальный статус бронирования.
     * По умолчанию CONFIRMED, так как администратор создаёт «вручную».
     */
    private BookingStatus status = BookingStatus.CONFIRMED;

    /** Примечание администратора */
    private String notes;

    /** ID выбранной опции оплаты (если у события несколько вариантов) */
    private Long priceOptionId;
}
