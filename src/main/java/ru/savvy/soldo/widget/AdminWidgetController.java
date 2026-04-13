package ru.savvy.soldo.widget;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigUpdateRequest;

/**
 * Административный API для управления конфигурацией Booking Widget.
 * Требует роль ADMIN.
 *
 * Base path: /admin/widget
 */
@RestController
@RequestMapping("/admin/widget")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Widget")
public class AdminWidgetController {

    private final WidgetService widgetService;

    /**
     * GET /admin/widget/config
     * Возвращает текущую конфигурацию виджета для тенанта авторизованного пользователя.
     */
    @GetMapping("/config")
    public WidgetConfigResponse getConfig(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return widgetService.getConfigByUserId(userId);
    }

    /**
     * PUT /admin/widget/config
     * Обновляет конфигурацию виджета для тенанта авторизованного пользователя.
     */
    @PutMapping("/config")
    public WidgetConfigResponse updateConfig(Authentication auth,
                                              @RequestBody WidgetConfigUpdateRequest req) {
        Long userId = Long.parseLong(auth.getName());
        return widgetService.updateConfig(userId, req);
    }
}
