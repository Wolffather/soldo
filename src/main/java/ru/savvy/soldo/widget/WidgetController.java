package ru.savvy.soldo.widget;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.widget.dto.*;

import java.util.List;

@RestController
@RequestMapping("/public/widget")
@RequiredArgsConstructor
@Tag(name = "Widget")
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping("/config")
    public WidgetConfigResponse getConfig() {
        return widgetService.getConfig();
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public WidgetConfigResponse updateConfig(@RequestBody WidgetConfigUpdateRequest req) {
        return widgetService.updateConfig(req);
    }

    @GetMapping("/categories")
    public List<WidgetCategoryResponse> getCategories() {
        return widgetService.getCategories();
    }

    @GetMapping("/events")
    public List<WidgetEventResponse> getEvents(@RequestParam(required = false) Long categoryId) {
        return widgetService.getEvents(categoryId);
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetBookingResponse createBooking(@Valid @RequestBody WidgetBookingRequest req) {
        return widgetService.createBooking(req);
    }
}
