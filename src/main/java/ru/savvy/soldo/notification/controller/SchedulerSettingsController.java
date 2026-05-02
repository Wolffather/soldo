package ru.savvy.soldo.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.notification.settings.SchedulerSettingsDTO;
import ru.savvy.soldo.notification.settings.SchedulerSettingsService;

@RestController
@RequestMapping("/admin/scheduler-settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SchedulerSettingsController {

    private final SchedulerSettingsService settingsService;

    @GetMapping
    public ResponseEntity<SchedulerSettingsDTO> getSettings() {
        return ResponseEntity.ok(settingsService.getSettingsDto());
    }

    @PutMapping
    public ResponseEntity<SchedulerSettingsDTO> updateSettings(@Valid @RequestBody SchedulerSettingsDTO dto) {
        return ResponseEntity.ok(settingsService.updateSettings(dto));
    }
}
