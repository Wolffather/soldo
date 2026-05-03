package ru.savvy.soldo.widget;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigUpdateRequest;

@RestController
@RequestMapping("/admin/widget")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Widget")
public class AdminWidgetController {

    private final WidgetService widgetService;

    @GetMapping("/config")
    public WidgetConfigResponse getConfig() {
        return widgetService.getConfig();
    }

    @PutMapping("/config")
    public WidgetConfigResponse updateConfig(@RequestBody WidgetConfigUpdateRequest req) {
        return widgetService.updateConfig(req);
    }
}
