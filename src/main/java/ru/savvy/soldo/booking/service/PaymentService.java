package ru.savvy.soldo.booking.service;

import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.booking.model.Booking;

import java.math.BigDecimal;

public interface PaymentService {

    /**
     * Applies payment status / amount changes to the booking,
     * sends a notification when fully paid, and returns the saved entity
     * for further response mapping by the caller.
     */
    Booking processPaymentUpdate(Long id, PaymentUpdateRequest request);

    BigDecimal getMonthlyRevenue();
}
