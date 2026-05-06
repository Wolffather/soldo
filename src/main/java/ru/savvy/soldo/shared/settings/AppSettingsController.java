package ru.savvy.soldo.shared.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AppSettingsController {

    private final AppSettingsService settingsService;

    @GetMapping
    public ResponseEntity<Map<String, String>> getAll() {
        return ResponseEntity.ok(settingsService.getAll());
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> saveAll(@RequestBody Map<String, String> settings) {
        settingsService.saveAll(settings);
        return ResponseEntity.ok(settingsService.getAll());
    }
}
