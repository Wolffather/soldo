package ru.savvy.soldo.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.booking.dto.AdminBookingRequest;
import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.booking.dto.BookingResponse;
import ru.savvy.soldo.booking.dto.BookingSummaryResponse;
import ru.savvy.soldo.booking.repository.BookingDocumentRepository;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.booking.model.EventBookingsSummary;
import ru.savvy.soldo.booking.model.PaymentStatus;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.booking.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.booking.service.BookingDocumentService;
import ru.savvy.soldo.booking.service.BookingService;
import ru.savvy.soldo.booking.service.PaymentService;
import ru.savvy.soldo.event.model.EventPriceOption;
import ru.savvy.soldo.event.repository.EventPriceOptionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final EventBookingSummaryRepository summaryRepository;
    private final PaymentService paymentService;
    private final BookingDocumentService bookingDocumentService;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final EventPriceOptionRepository priceOptionRepository;

    @Override
    @Transactional
    public BookingResponse createByAdmin(AdminBookingRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        summaryRepository.findByEventId(event.getId()).ifPresent(summary -> {
            if (summary.getNumOfParticipants() <= 0) {
                throw new IllegalOperationException("Нет свободных мест для события");
            }
        });

        PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;
        BigDecimal amountDue = BigDecimal.ZERO;
        LocalDate paymentDeadline = null;

        EventPriceOption priceOption = null;
        if (request.getPriceOptionId() != null) {
            priceOption = priceOptionRepository.findById(request.getPriceOptionId()).orElse(null);
        }

        BigDecimal effectivePrice = priceOption != null ? priceOption.getPrice() : event.getPrice();

        if (effectivePrice != null && effectivePrice.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PENDING;
            amountDue = effectivePrice;
            paymentDeadline = LocalDate.now().plusDays(7);
        }

        Booking booking = Booking.builder()
                .event(event)
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .guestEmail(request.getGuestEmail())
                .paymentStatus(paymentStatus)
                .amountDue(amountDue)
                .paymentDeadline(paymentDeadline)
                .hasCertificate(request.isHasCertificate())
                .notes(request.getNotes())
                .priceOption(priceOption)
                .build();

        booking = bookingRepository.save(booking);
        summaryRepository.findByEventId(event.getId()).ifPresent(s -> summaryRepository.refreshSummary(event.getId()));

        bookingDocumentService.createDocumentsForBooking(booking);
        if (booking.getGuestEmail() != null && !booking.getGuestEmail().isBlank()) {
            bookingDocumentService.sendDocumentEmail(booking);
        }

        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getByEventId(Long eventId) {
        return bookingRepository.findByEventId(eventId).stream()
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
    public BookingResponse cancel(Long id) {
        Booking booking = EntityFinder.findOrThrow(bookingRepository.findById(id), "Бронирование не найдено: " + id);

        if (booking.isCancelled()) {
            throw new IllegalOperationException("Бронирование уже отменено");
        }

        booking.setCancelled(true);
        booking = bookingRepository.save(booking);

        bookingDocumentService.archiveDocumentsForBooking(booking.getId());
        Booking finalBooking = booking;
        summaryRepository.findByEventId(booking.getEvent().getId()).ifPresent(s -> summaryRepository.refreshSummary(finalBooking.getEvent().getId()));

        return toResponse(booking);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Booking booking = EntityFinder.findOrThrow(bookingRepository.findById(id), "Бронирование не найдено: " + id);
        Long eventId = booking.getEvent().getId();

        bookingDocumentRepository.deleteByBookingId(id);
        bookingRepository.delete(booking);

        summaryRepository.findByEventId(eventId).ifPresent(s -> summaryRepository.refreshSummary(eventId));
    }

    @Override
    @Transactional
    public BookingResponse updatePayment(Long id, PaymentUpdateRequest request) {
        return toResponse(paymentService.processPaymentUpdate(id, request));
    }

    @Override
    public BigDecimal getMonthlyRevenue() {
        return paymentService.getMonthlyRevenue();
    }

    @Override
    @Transactional
    public BookingResponse sendDocuments(Long id) {
        Booking booking = EntityFinder.findOrThrow(bookingRepository.findById(id), "Бронирование не найдено: " + id);
        bookingDocumentService.sendDocumentEmail(booking);
        return toResponse(booking);
    }

    private void notifyBooker(Booking booking, String message) {
        // SMS/push notifications — to be implemented
    }

    private BookingResponse toResponse(Booking b) {
        // Сводка по документам бронирования
        int documentTotal = bookingDocumentRepository.countActiveByBookingId(b.getId());
        int documentSigned = bookingDocumentRepository.countActiveSignedByBookingId(b.getId());
        int documentRequireSignature = bookingDocumentRepository.countActiveRequiresSignatureByBookingId(b.getId());

        return BookingResponse.builder()
                .id(b.getId())
                .guestName(b.getGuestName())
                .guestPhone(b.getGuestPhone())
                .guestEmail(b.getGuestEmail())
                .eventId(b.getEvent().getId())
                .eventTitle(b.getEvent().getTitle())
                .status(b.isCancelled() ? "CANCELLED" : "CONFIRMED")
                .paymentStatus(b.getPaymentStatus() != null ? b.getPaymentStatus().name() : null)
                .amountDue(b.getAmountDue())
                .amountPaid(b.getAmountPaid())
                .paymentDeadline(b.getPaymentDeadline())
                .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null)
                .hasCertificate(b.isHasCertificate())
                .notes(b.getNotes())
                .priceOptionId(b.getPriceOption() != null ? b.getPriceOption().getId() : null)
                .priceOptionName(b.getPriceOption() != null ? b.getPriceOption().getName() : null)
                .documentTotal(documentTotal)
                .documentSigned(documentSigned)
                .documentRequireSignature(documentRequireSignature)
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
