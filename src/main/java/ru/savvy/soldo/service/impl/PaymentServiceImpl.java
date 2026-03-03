package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.request.PaymentUpdateRequest;
import ru.savvy.soldo.exception.EntityFinder;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.enums.NotificationType;
import ru.savvy.soldo.model.enums.PaymentStatus;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.service.NotificationService;
import ru.savvy.soldo.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Booking processPaymentUpdate(Long id, PaymentUpdateRequest request) {
        Booking booking = EntityFinder.findOrThrow(
                bookingRepository.findByIdWithCategory(id), "Бронирование не найдено: " + id);

        PaymentStatus newStatus = PaymentStatus.valueOf(request.getPaymentStatus());
        booking.setPaymentStatus(newStatus);

        if (request.getAmountPaid() != null) {
            booking.setAmountPaid(request.getAmountPaid());
        }

        if (newStatus == PaymentStatus.PAID) {
            booking.setPaymentDate(LocalDateTime.now());
            booking.setAmountPaid(booking.getAmountDue());

            notificationService.createAndSend(
                    booking.getUser().getId(),
                    booking.getEvent().getId(),
                    booking.getId(),
                    NotificationType.BOOKING_CONFIRMED,
                    String.format("💰 Оплата за <b>%s</b> получена. Спасибо!",
                                  booking.getEvent().getTitle()));
        }

        return bookingRepository.save(booking);
    }

    @Override
    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        return bookingRepository.getMonthlyRevenue(startOfMonth);
    }
}
