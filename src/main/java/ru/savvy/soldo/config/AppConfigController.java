package ru.savvy.soldo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.config.dto.AppConfigResponse;
import ru.savvy.soldo.config.dto.AppConfigUpdateRequest;

@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class AppConfigController {

    private final AppConfigService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AppConfigResponse get() {
        return service.get();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AppConfigResponse update(@RequestBody AppConfigUpdateRequest request) {
        return service.update(request);
    }
}
