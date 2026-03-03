package ru.savvy.soldo.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.notification.model.NotificationType;
import ru.savvy.soldo.booking.model.PaymentStatus;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.notification.service.NotificationService;
import ru.savvy.soldo.booking.service.PaymentService;

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
