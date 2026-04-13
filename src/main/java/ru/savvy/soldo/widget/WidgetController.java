package ru.savvy.soldo.widget;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.savvy.soldo.widget.dto.WidgetBookingRequest;
import ru.savvy.soldo.widget.dto.WidgetBookingResponse;
import ru.savvy.soldo.widget.dto.WidgetCategoryResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetEventResponse;

import java.util.List;

/**
 * Публичный API для Booking Widget (встраиваемого виджета бронирования).
 * Все эндпоинты не требуют JWT-авторизации.
 *
 * Base path: /public/widget
 */
@RestController
@RequestMapping("/public/widget")
@RequiredArgsConstructor
@Tag(name = "Widget")
public class WidgetController {

    private final WidgetService widgetService;

    /**
     * GET /public/widget/config?tenantSlug=sol-camp
     * Возвращает конфигурацию внешнего вида виджета для тенанта.
     */
    @GetMapping("/config")
    public WidgetConfigResponse getConfig(@RequestParam String tenantSlug) {
        return widgetService.getConfig(tenantSlug);
    }

    /**
     * GET /public/widget/categories?tenantSlug=sol-camp
     * Возвращает категории событий с активными предстоящими событиями.
     */
    @GetMapping("/categories")
    public List<WidgetCategoryResponse> getCategories(@RequestParam String tenantSlug) {
        return widgetService.getCategories(tenantSlug);
    }

    /**
     * GET /public/widget/events?tenantSlug=sol-camp&categoryId=3
     * Возвращает опубликованные предстоящие события (опционально фильтрует по категории).
     */
    @GetMapping("/events")
    public List<WidgetEventResponse> getEvents(
            @RequestParam String tenantSlug,
            @RequestParam(required = false) Long categoryId) {
        return widgetService.getEvents(tenantSlug, categoryId);
    }

    /**
     * POST /public/widget/bookings
     * Создаёт гостевое бронирование через виджет.
     */
    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetBookingResponse createBooking(@Valid @RequestBody WidgetBookingRequest req) {
        return widgetService.createBooking(req);
    }
}
