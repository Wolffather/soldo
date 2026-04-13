package ru.savvy.soldo.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingStatus;
import ru.savvy.soldo.booking.model.PaymentStatus;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.bot.dto.BotBookingRequest;
import ru.savvy.soldo.bot.dto.BotBookingResponse;
import ru.savvy.soldo.bot.dto.BotCategoryResponse;
import ru.savvy.soldo.bot.dto.BotEventResponse;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventCategory;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.event.repository.EventCategoryRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.booking.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.tenant.TenantRepository;
import ru.savvy.soldo.tenant.model.Tenant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotBookingServiceImpl implements BotBookingService {

    private final TenantRepository tenantRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventBookingSummaryRepository summaryRepository;

    @Override
    public List<BotCategoryResponse> getCategories(String tenantSlug) {
        Tenant tenant = resolveTenant(tenantSlug);
        // Возвращаем категории, по которым есть хотя бы одно опубликованное предстоящее событие
        List<Long> activeCategoryIds = eventRepository
                .findUpcomingCategoryIdsByTenantId(tenant.getId(), LocalDate.now());

        return eventCategoryRepository.findAllById(activeCategoryIds).stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Override
    public List<BotEventResponse> getEventsByCategory(String tenantSlug, Long categoryId) {
        Tenant tenant = resolveTenant(tenantSlug);
        return eventRepository
                .findPublishedUpcomingByCategoryAndTenant(categoryId, tenant.getId(), LocalDate.now())
                .stream()
                .map(e -> toEventResponse(e, summaryRepository.findAvailableSeatsByEventId(e.getId())))
                .toList();
    }

    @Override
    public BotEventResponse getEventById(String tenantSlug, Long eventId) {
        Tenant tenant = resolveTenant(tenantSlug);
        Event event = eventRepository.findByIdWithCategory(eventId)
                .filter(e -> e.getTenantId().equals(tenant.getId()))
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        Integer available = summaryRepository.findAvailableSeatsByEventId(eventId);
        return toEventResponse(event, available);
    }

    @Override
    @Transactional
    public BotBookingResponse createBooking(BotBookingRequest request) {
        Tenant tenant = resolveTenant(request.getTenantSlug());

        Event event = eventRepository.findByIdWithCategory(request.getEventId())
                .filter(e -> e.getTenantId().equals(tenant.getId()))
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));

        // Проверяем наличие мест
        summaryRepository.findByEventId(event.getId()).ifPresent(summary -> {
            if (summary.getNumOfParticipants() <= 0) {
                throw new IllegalOperationException("Нет свободных мест");
            }
        });

        // Проверяем, нет ли уже активного бронирования с этим telegramId на это событие
        boolean alreadyBooked = bookingRepository
                .existsByTelegramChatIdAndEventIdAndStatusNot(
                        request.getTelegramId(), event.getId(), BookingStatus.CANCELLED);
        if (alreadyBooked) {
            throw new IllegalOperationException("Вы уже записаны на это мероприятие");
        }

        BigDecimal amountDue = BigDecimal.ZERO;
        PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;
        LocalDate paymentDeadline = null;

        if (event.getPrice() != null && event.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            amountDue = event.getPrice();
            paymentStatus = PaymentStatus.PENDING;
            paymentDeadline = LocalDate.now().plusDays(7);
        }

        Booking booking = Booking.builder()
                .event(event)
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .guestEmail(request.getGuestEmail())
                .telegramChatId(request.getTelegramId())
                .status(BookingStatus.PENDING)
                .paymentStatus(paymentStatus)
                .amountDue(amountDue)
                .paymentDeadline(paymentDeadline)
                .tenantId(tenant.getId())
                .build();

        booking = bookingRepository.save(booking);
        summaryRepository.onCreatePending(event.getId());

        return toBookingResponse(booking);
    }

    @Override
    public List<BotBookingResponse> getMyBookings(String tenantSlug, Long telegramId) {
        Tenant tenant = resolveTenant(tenantSlug);
        return bookingRepository
                .findActiveByTelegramChatIdAndTenantId(telegramId, tenant.getId())
                .stream()
                .map(this::toBookingResponse)
                .toList();
    }

    @Override
    @Transactional
    public BotBookingResponse cancelBooking(Long bookingId, Long telegramId) {
        Booking booking = bookingRepository.findByIdWithCategory(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!telegramId.equals(booking.getTelegramChatId())) {
            throw new IllegalOperationException("Нет доступа к этому бронированию");
        }
        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new IllegalOperationException("Бронирование уже отменено");
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        if (previousStatus == BookingStatus.CONFIRMED) {
            summaryRepository.onCancelFromConfirmed(booking.getEvent().getId());
        } else {
            summaryRepository.onCancelFromPending(booking.getEvent().getId());
        }

        return toBookingResponse(booking);
    }

    @Override
    @Transactional
    public BotBookingResponse bindTelegramChat(String tenantSlug, Long bookingId, Long telegramId) {
        Tenant tenant = resolveTenant(tenantSlug);
        Booking booking = bookingRepository.findByIdWithCategory(bookingId)
                .filter(b -> tenant.getId().equals(b.getTenantId()))
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (booking.getTelegramChatId() == null) {
            booking.setTelegramChatId(telegramId);
            booking = bookingRepository.save(booking);
        } else if (!telegramId.equals(booking.getTelegramChatId())) {
            throw new IllegalOperationException(
                    "Это бронирование привязано к другому Telegram-аккаунту");
        }

        return toBookingResponse(booking);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private Tenant resolveTenant(String slug) {
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Тенант не найден: " + slug));
    }

    private BotCategoryResponse toCategoryResponse(EventCategory c) {
        return BotCategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .format(c.getFormat() != null ? c.getFormat().name() : null)
                .iconUrl(c.getIconUrl())
                .build();
    }

    private BotEventResponse toEventResponse(Event e, Integer availableSpots) {
        return BotEventResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .price(e.getPrice())
                .availableSpots(availableSpots)
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
                .build();
    }

    private BotBookingResponse toBookingResponse(Booking b) {
        return BotBookingResponse.builder()
                .id(b.getId())
                .eventId(b.getEvent().getId())
                .eventTitle(b.getEvent().getTitle())
                .startDate(b.getEvent().getStartDate())
                .endDate(b.getEvent().getEndDate())
                .status(b.getStatus().name())
                .paymentStatus(b.getPaymentStatus() != null ? b.getPaymentStatus().name() : null)
                .amountDue(b.getAmountDue())
                .build();
    }
}
