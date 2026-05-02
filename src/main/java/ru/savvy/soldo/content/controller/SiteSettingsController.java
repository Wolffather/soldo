package ru.savvy.soldo.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.content.service.SiteSettingsService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    /** Public: все настройки сайта */
    @GetMapping("/public/site-settings")
    public ResponseEntity<Map<String, String>> getAll() {
        return ResponseEntity.ok(siteSettingsService.getAll());
    }

    /** Admin: сохранить/обновить настройку */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/site-settings/{key:.+}")
    public ResponseEntity<Void> upsert(
            @PathVariable String key,
            @RequestBody Map<String, String> body) {
        siteSettingsService.upsert(key, body.get("value"));
        return ResponseEntity.ok().build();
    }

    /** Admin: удалить настройку */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/site-settings/{key:.+}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        siteSettingsService.delete(key);
        return ResponseEntity.noContent().build();
    }
}
