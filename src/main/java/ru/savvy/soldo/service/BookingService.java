package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.*;
import ru.savvy.soldo.dto.request.PaymentUpdateRequest;
import ru.savvy.soldo.dto.response.BookingResponse;
import ru.savvy.soldo.dto.response.BookingSummaryResponse;

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