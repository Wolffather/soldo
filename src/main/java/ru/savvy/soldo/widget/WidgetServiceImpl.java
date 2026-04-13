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
import ru.savvy.soldo.event.model.EventCategory;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.event.repository.EventCategoryRepository;
import ru.savvy.soldo.event.repository.EventRepository;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.tenant.TenantRepository;
import ru.savvy.soldo.tenant.model.Tenant;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.widget.dto.WidgetBookingRequest;
import ru.savvy.soldo.widget.dto.WidgetBookingResponse;
import ru.savvy.soldo.widget.dto.WidgetCategoryResponse;
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

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final WidgetConfigRepository widgetConfigRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventBookingSummaryRepository summaryRepository;

    @Override
    @Transactional
    public WidgetConfigResponse getConfig(String tenantSlug) {
        Tenant tenant = resolveTenant(tenantSlug);
        WidgetConfig config = widgetConfigRepository.findById(tenant.getId())
                .orElseGet(() -> createDefaultConfig(tenant));
        return toConfigResponse(config, tenant);
    }

    @Override
    @Transactional
    public WidgetConfigResponse getConfigByUserId(Long userId) {
        Long tenantId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                .getTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));
        WidgetConfig config = widgetConfigRepository.findById(tenantId)
                .orElseGet(() -> createDefaultConfig(tenant));
        return toConfigResponse(config, tenant);
    }

    @Override
    @Transactional
    public WidgetConfigResponse updateConfig(Long userId, WidgetConfigUpdateRequest req) {
        Long tenantId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                .getTenantId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));

        WidgetConfig config = widgetConfigRepository.findById(tenantId)
                .orElseGet(() -> createDefaultConfig(tenant));

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

        config = widgetConfigRepository.save(config);
        return toConfigResponse(config, tenant);
    }

    @Override
    public List<WidgetCategoryResponse> getCategories(String tenantSlug) {
        Tenant tenant = resolveTenant(tenantSlug);
        List<Long> activeCategoryIds = eventRepository
                .findUpcomingCategoryIdsByTenantId(tenant.getId(), LocalDate.now());
        return eventCategoryRepository.findAllById(activeCategoryIds).stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Override
    public List<WidgetEventResponse> getEvents(String tenantSlug, Long categoryId) {
        Tenant tenant = resolveTenant(tenantSlug);
        if (categoryId != null) {
            return eventRepository
                    .findPublishedUpcomingByCategoryAndTenant(categoryId, tenant.getId(), LocalDate.now())
                    .stream()
                    .map(e -> toEventResponse(e, summaryRepository.findAvailableSeatsByEventId(e.getId())))
                    .toList();
        }
        // No categoryId — return all published upcoming events for tenant
        List<Long> categoryIds = eventRepository
                .findUpcomingCategoryIdsByTenantId(tenant.getId(), LocalDate.now());
        return categoryIds.stream()
                .flatMap(catId -> eventRepository
                        .findPublishedUpcomingByCategoryAndTenant(catId, tenant.getId(), LocalDate.now())
                        .stream())
                .map(e -> toEventResponse(e, summaryRepository.findAvailableSeatsByEventId(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public WidgetBookingResponse createBooking(WidgetBookingRequest req) {
        Tenant tenant = resolveTenant(req.getTenantSlug());

        Event event = eventRepository.findByIdWithCategory(req.getEventId())
                .filter(e -> e.getTenantId().equals(tenant.getId()))
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));

        // Check available spots
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
                .tenantId(tenant.getId())
                .build();

        booking = bookingRepository.save(booking);
        summaryRepository.onCreatePending(event.getId());

        String successMessage = widgetConfigRepository.findById(tenant.getId())
                .map(WidgetConfig::getSuccessMessage)
                .orElse("Бронирование успешно создано! Мы свяжемся с вами.");

        return toBookingResponse(booking, successMessage);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private Tenant resolveTenant(String slug) {
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Тенант не найден: " + slug));
    }

    private WidgetConfig createDefaultConfig(Tenant tenant) {
        WidgetConfig config = WidgetConfig.builder()
                .tenantId(tenant.getId())
                .tenant(tenant)
                .build();
        return widgetConfigRepository.save(config);
    }

    private WidgetConfigResponse toConfigResponse(WidgetConfig config, Tenant tenant) {
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
                .tenantSlug(tenant.getSlug())
                .tenantName(tenant.getName())
                .build();
    }

    private WidgetCategoryResponse toCategoryResponse(EventCategory c) {
        return WidgetCategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .iconUrl(c.getIconUrl())
                .format(c.getFormat() != null ? c.getFormat().name() : null)
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
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
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
