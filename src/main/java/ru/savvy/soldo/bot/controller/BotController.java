package ru.savvy.soldo.bot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.bot.dto.BotBookingRequest;
import ru.savvy.soldo.bot.dto.BotBookingResponse;
import ru.savvy.soldo.bot.dto.BotCategoryResponse;
import ru.savvy.soldo.bot.dto.BotEventResponse;
import ru.savvy.soldo.bot.service.BotBookingService;

import java.util.List;

/**
 * Публичный API для Telegram-бота.
 * Все эндпоинты не требуют JWT-авторизации — идентификация через telegramId.
 *
 * Base path: /public/bot
 */
@RestController
@RequestMapping("/public/bot")
@RequiredArgsConstructor
public class BotController {

    private final BotBookingService botBookingService;

    // ─── Categories ──────────────────────────────────────────────────────────

    /**
     * GET /public/bot/categories?tenantSlug=savvy-camp
     * Возвращает категории событий с активными предстоящими событиями.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<BotCategoryResponse>> getCategories(
            @RequestParam String tenantSlug) {
        return ResponseEntity.ok(botBookingService.getCategories(tenantSlug));
    }

    // ─── Events ──────────────────────────────────────────────────────────────

    /**
     * GET /public/bot/events?tenantSlug=savvy-camp&categoryId=1
     * Возвращает опубликованные предстоящие события по категории.
     */
    @GetMapping("/events")
    public ResponseEntity<List<BotEventResponse>> getEventsByCategory(
            @RequestParam String tenantSlug,
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(botBookingService.getEventsByCategory(tenantSlug, categoryId));
    }

    /**
     * GET /public/bot/events/{id}?tenantSlug=savvy-camp
     * Возвращает событие по ID.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<BotEventResponse> getEventById(
            @PathVariable Long id,
            @RequestParam String tenantSlug) {
        return ResponseEntity.ok(botBookingService.getEventById(tenantSlug, id));
    }

    // ─── Bookings ─────────────────────────────────────────────────────────────

    /**
     * POST /public/bot/bookings
     * Создаёт гостевое бронирование от Telegram-пользователя.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BotBookingResponse> createBooking(
            @Valid @RequestBody BotBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(botBookingService.createBooking(request));
    }

    /**
     * GET /public/bot/bookings?tenantSlug=savvy-camp&telegramId=123456789
     * Возвращает активные бронирования пользователя по Telegram chat ID.
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BotBookingResponse>> getMyBookings(
            @RequestParam String tenantSlug,
            @RequestParam Long telegramId) {
        return ResponseEntity.ok(botBookingService.getMyBookings(tenantSlug, telegramId));
    }

    /**
     * POST /public/bot/bookings/{id}/cancel?telegramId=123456789
     * Отменяет бронирование. Проверяет принадлежность telegramId.
     */
    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<BotBookingResponse> cancelBooking(
            @PathVariable Long id,
            @RequestParam Long telegramId) {
        return ResponseEntity.ok(botBookingService.cancelBooking(id, telegramId));
    }
}
