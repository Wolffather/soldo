package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.request.PaymentUpdateRequest;
import ru.savvy.soldo.model.Booking;

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
