package ru.savvy.soldo.tenant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.tenant.dto.TelegramBotSetupRequest;
import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;

@RestController
@RequestMapping("/admin/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * GET /admin/tenant
     * Возвращает информацию о тенанте текущего администратора:
     * название, слаг, тарифный план, лимиты, конфигурация лейблов.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<TenantResponse> getCurrent(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.getCurrentTenant(userId));
    }

    /**
     * PUT /admin/tenant/config
     * Обновляет конфигурацию тенанта (лейблы, название, домен).
     * Только ADMIN.
     */
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> updateConfig(
            Authentication auth,
            @Valid @RequestBody TenantConfigUpdateRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.updateConfig(userId, request));
    }

    /**
     * POST /admin/tenant/telegram-bot
     * Подключает Telegram-бота: сохраняет токен и регистрирует webhook.
     */
    @PostMapping("/telegram-bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> connectTelegramBot(
            Authentication auth,
            @Valid @RequestBody TelegramBotSetupRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.connectTelegramBot(userId, request));
    }

    /**
     * DELETE /admin/tenant/telegram-bot
     * Отключает Telegram-бота: очищает токен и удаляет webhook.
     */
    @DeleteMapping("/telegram-bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> disconnectTelegramBot(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.disconnectTelegramBot(userId));
    }
}
