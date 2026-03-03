package ru.savvy.soldo.booking.service;

import ru.savvy.soldo.booking.dto.BookingDTO;
import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.booking.dto.BookingResponse;
import ru.savvy.soldo.booking.dto.BookingSummaryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {

    BookingResponse create(BookingDTO dto, Long userId);

    List<BookingResponse> getByEventId(Long eventId);

    List<BookingResponse> getByUserId(Long userId);

    BookingSummaryResponse getSummary(Long eventId);

    List<BookingSummaryResponse> getAllSummaries();

    BookingResponse confirm(Long id);

    BookingResponse cancel(Long id);

    BookingResponse updatePayment(Long id, PaymentUpdateRequest request);

    BigDecimal getMonthlyRevenue();
}