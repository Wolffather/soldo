package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.*;
import ru.savvy.soldo.dto.request.PaymentUpdateRequest;
import ru.savvy.soldo.dto.response.BookingResponse;
import ru.savvy.soldo.dto.response.BookingSummaryResponse;
import ru.savvy.soldo.exception.IllegalOperationException;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.model.enums.BookingStatus;
import ru.savvy.soldo.model.enums.NotificationType;
import ru.savvy.soldo.model.enums.PaymentStatus;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.BookingService;
import ru.savvy.soldo.service.NotificationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingResponse create(BookingDTO dto, Long userId) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Проверка дублирования
        if (bookingRepository.existsByUserIdAndEventIdAndStatusNot(userId, event.getId(), BookingStatus.CANCELLED)) {
            throw new IllegalOperationException("Бронирование уже существует");
        }

        // Проверка возраста
        if (user.getBirthDate() != null && event.getMinAge() != null) {
            int age = LocalDate.now().getYear() - user.getBirthDate().getYear();
            if (age < event.getMinAge() || (event.getMaxAge() != null && age > event.getMaxAge())) {
                throw new IllegalOperationException(
                        String.format("Возраст не подходит. Требуется: %d-%d лет",
                                      event.getMinAge(), event.getMaxAge()));
            }
        }

        // Определяем статус оплаты
        PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;
        BigDecimal amountDue = BigDecimal.ZERO;
        LocalDate paymentDeadline = null;

        if (event.getPrice() != null && event.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PENDING;
            amountDue = event.getPrice();
            paymentDeadline = LocalDate.now().plusDays(7);
        }

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .status(BookingStatus.PENDING)
                .paymentStatus(paymentStatus)
                .amountDue(amountDue)
                .paymentDeadline(paymentDeadline)
                .build();

        booking = bookingRepository.save(booking);

        // Уведомление
        notificationService.createAndSend(
                userId, event.getId(), booking.getId(),
                NotificationType.BOOKING_CONFIRMED,
                String.format("✅ Вы записались на <b>%s</b>!\nСтатус: ожидает подтверждения",
                              event.getTitle()));

        return toResponse(booking);
    }

    @Override
    public List<BookingResponse> getByEventId(Long eventId) {
        return bookingRepository.findByEventId(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BookingSummaryResponse getSummary(Long eventId) {
        EventBookingsSummary summary = summaryRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Сводка не найдена"));
        return toSummaryResponse(summary);
    }

    @Override
    public List<BookingSummaryResponse> getAllSummaries() {
        return summaryRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse confirm(Long id) {
        Booking booking = findOrThrow(id);

        if (!BookingStatus.PENDING.equals(booking.getStatus())) {
            throw new IllegalOperationException("Можно подтвердить только бронирование в статусе PENDING");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        // Уведомление
        notificationService.createAndSend(
                booking.getUser().getId(),
                booking.getEvent().getId(),
                booking.getId(),
                NotificationType.BOOKING_CONFIRMED,
                String.format("✅ Ваше бронирование на <b>%s</b> подтверждено!",
                              booking.getEvent().getTitle()));

        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = findOrThrow(id);

        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new IllegalOperationException("Бронирование уже отменено");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        // Уведомление
        notificationService.createAndSend(
                booking.getUser().getId(),
                booking.getEvent().getId(),
                booking.getId(),
                NotificationType.BOOKING_CANCELLED,
                String.format("❌ Ваше бронирование на <b>%s</b> отменено.",
                              booking.getEvent().getTitle()));

        return toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse updatePayment(Long id, PaymentUpdateRequest request) {
        Booking booking = findOrThrow(id);

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

        return toResponse(bookingRepository.save(booking));
    }

    private Booking findOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + id));
    }

    private BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .userName(b.getUser().getFirstName())
                .eventId(b.getEvent().getId())
                .eventTitle(b.getEvent().getTitle())
                .status(String.valueOf(b.getStatus()))
                .paymentStatus(b.getPaymentStatus() != null ? b.getPaymentStatus().name() : null)
                .amountDue(b.getAmountDue())
                .amountPaid(b.getAmountPaid())
                .paymentDeadline(b.getPaymentDeadline())
                .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null)
                .build();
    }

    private BookingSummaryResponse toSummaryResponse(EventBookingsSummary s) {
        return BookingSummaryResponse.builder()
                .eventId(s.getEvent().getId())
                .eventTitle(s.getEvent().getTitle())
                .totalBookings(Long.valueOf(s.getTotalBookings()))
                .confirmedBookings(Long.valueOf(s.getConfirmedBookings()))
                .pendingBookings(s.getPendingBookings())
                .cancelledBookings(s.getCancelledBookings())
                .availableSeats(Long.valueOf(s.getNumOfParticipants()))
                .build();
    }
}