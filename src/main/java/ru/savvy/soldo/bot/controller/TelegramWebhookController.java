package ru.savvy.soldo.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savvy.soldo.bot.service.TelegramWebhookService;

import java.util.Map;

/**
 * Принимает webhook-обновления от Telegram для бота конкретного тенанта.
 *
 * URL формируется при регистрации бота: {@code POST /public/bot/webhook/{tenantSlug}}
 * Telegram передаёт секрет в заголовке {@code X-Telegram-Bot-Api-Secret-Token}.
 */
@Slf4j
@RestController
@RequestMapping("/public/bot/webhook")
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final TelegramWebhookService telegramWebhookService;

    @GetMapping("/{tenantSlug}")
    public ResponseEntity<Void> healthCheck(@PathVariable String tenantSlug) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tenantSlug}")
    public ResponseEntity<Void> handleUpdate(
            @PathVariable String tenantSlug,
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secret,
            @RequestBody Map<String, Object> update) {

        try {
            telegramWebhookService.handleUpdate(tenantSlug, secret, update);
        } catch (Exception e) {
            log.error("Ошибка обработки Telegram webhook для тенанта {}: {}", tenantSlug, e.getMessage());
        }
        // Telegram переотправляет update при не-200, поэтому всегда отвечаем 200
        return ResponseEntity.ok().build();
    }
}
