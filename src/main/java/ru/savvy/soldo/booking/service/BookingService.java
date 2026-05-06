package ru.savvy.soldo.booking.service;

import ru.savvy.soldo.booking.dto.AdminBookingRequest;
import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.booking.dto.BookingResponse;
import ru.savvy.soldo.booking.dto.BookingSummaryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {

    /** Администратор создаёт бронирование вручную (всегда гостевое — без аккаунта). */
    BookingResponse createByAdmin(AdminBookingRequest request);

    List<BookingResponse> getByEventId(Long eventId);

    BookingSummaryResponse getSummary(Long eventId);

    List<BookingSummaryResponse> getAllSummaries();

    BookingResponse confirm(Long id);

    BookingResponse cancel(Long id);

    BookingResponse updatePayment(Long id, PaymentUpdateRequest request);

    BigDecimal getMonthlyRevenue();

    /** Sends (or re-sends) document email for a booking. Returns updated booking. */
    BookingResponse sendDocuments(Long id);
}
