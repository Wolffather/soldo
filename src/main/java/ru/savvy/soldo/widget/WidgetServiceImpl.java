package ru.savvy.soldo.widget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingStatus;
import ru.savvy.soldo.booking.model.PaymentStatus;
import ru.savvy.soldo.booking.repository.BookingRepository;
import ru.savvy.soldo.booking.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.widget.dto.WidgetBookingRequest;
import ru.savvy.soldo.widget.dto.WidgetBookingResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigUpdateRequest;
import ru.savvy.soldo.widget.dto.WidgetEventResponse;
import ru.savvy.soldo.widget.model.WidgetConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WidgetServiceImpl implements WidgetService {

    private static final Long CONFIG_ID = 1L;

    private final WidgetConfigRepository widgetConfigRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventBookingSummaryRepository summaryRepository;

    @Override
    @Transactional
    public WidgetConfigResponse getConfig() {
        return toConfigResponse(loadOrCreateConfig());
    }

    @Override
    @Transactional
    public WidgetConfigResponse updateConfig(WidgetConfigUpdateRequest req) {
        WidgetConfig config = loadOrCreateConfig();
        if (req.getPrimaryColor() != null) config.setPrimaryColor(req.getPrimaryColor());
        if (req.getBackgroundColor() != null) config.setBackgroundColor(req.getBackgroundColor());
        if (req.getTextColor() != null) config.setTextColor(req.getTextColor());
        if (req.getButtonTextColor() != null) config.setButtonTextColor(req.getButtonTextColor());
        if (req.getBorderRadius() != null) config.setBorderRadius(req.getBorderRadius());
        if (req.getFontFamily() != null) config.setFontFamily(req.getFontFamily());
        if (req.getShowCategoryStep() != null) config.setShowCategoryStep(req.getShowCategoryStep());
        if (req.getSuccessMessage() != null) config.setSuccessMessage(req.getSuccessMessage());
        if (req.getCustomCss() != null) config.setCustomCss(req.getCustomCss());
        if (req.getCategoryStepTitle() != null) config.setCategoryStepTitle(req.getCategoryStepTitle());
        if (req.getButtonLabel() != null) config.setButtonLabel(req.getButtonLabel());
        return toConfigResponse(widgetConfigRepository.save(config));
    }

    @Override
    public List<WidgetEventResponse> getEvents(Long categoryId) {
        return eventRepository.findWidgetEvents(LocalDate.now())
                .stream()
                .map(e -> toEventResponse(e, summaryRepository.findAvailableSeatsByEventId(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public WidgetBookingResponse createBooking(WidgetBookingRequest req) {
        Event event = eventRepository.findById(req.getEventId())
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));

        summaryRepository.findByEventId(event.getId()).ifPresent(summary -> {
            if (summary.getNumOfParticipants() <= 0) {
                throw new IllegalOperationException("Нет свободных мест");
            }
        });

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
                .guestName(req.getGuestName())
                .guestPhone(req.getGuestPhone())
                .guestEmail(req.getGuestEmail())
                .notes(req.getNotes())
                .status(BookingStatus.PENDING)
                .paymentStatus(paymentStatus)
                .amountDue(amountDue)
                .paymentDeadline(paymentDeadline)
                .build();

        booking = bookingRepository.save(booking);
        summaryRepository.onCreatePending(event.getId());

        String successMessage = widgetConfigRepository.findById(CONFIG_ID)
                .map(WidgetConfig::getSuccessMessage)
                .orElse("Бронирование успешно создано! Мы свяжемся с вами.");

        return toBookingResponse(booking, successMessage);
    }

    private WidgetConfig loadOrCreateConfig() {
        return widgetConfigRepository.findById(CONFIG_ID).orElseGet(() ->
                widgetConfigRepository.save(WidgetConfig.builder().id(CONFIG_ID).build()));
    }

    private WidgetConfigResponse toConfigResponse(WidgetConfig config) {
        return WidgetConfigResponse.builder()
                .primaryColor(config.getPrimaryColor())
                .backgroundColor(config.getBackgroundColor())
                .textColor(config.getTextColor())
                .buttonTextColor(config.getButtonTextColor())
                .borderRadius(config.getBorderRadius())
                .fontFamily(config.getFontFamily())
                .showCategoryStep(config.getShowCategoryStep())
                .successMessage(config.getSuccessMessage())
                .customCss(config.getCustomCss())
                .categoryStepTitle(config.getCategoryStepTitle())
                .buttonLabel(config.getButtonLabel())
                .build();
    }

    private WidgetEventResponse toEventResponse(Event e, Integer availableSpots) {
        return WidgetEventResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .price(e.getPrice())
                .availableSpots(availableSpots)
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .build();
    }

    private WidgetBookingResponse toBookingResponse(Booking b, String successMessage) {
        return WidgetBookingResponse.builder()
                .id(b.getId())
                .eventTitle(b.getEvent().getTitle())
                .startDate(b.getEvent().getStartDate())
                .endDate(b.getEvent().getEndDate())
                .amountDue(b.getAmountDue())
                .status(b.getStatus().name())
                .successMessage(successMessage)
                .build();
    }
}
