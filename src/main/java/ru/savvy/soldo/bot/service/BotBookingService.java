package ru.savvy.soldo.bot.service;

import ru.savvy.soldo.bot.dto.BotBookingRequest;
import ru.savvy.soldo.bot.dto.BotBookingResponse;
import ru.savvy.soldo.bot.dto.BotCategoryResponse;
import ru.savvy.soldo.bot.dto.BotEventResponse;

import java.util.List;

public interface BotBookingService {

    /** Возвращает категории событий для тенанта. */
    List<BotCategoryResponse> getCategories(String tenantSlug);

    /** Возвращает опубликованные предстоящие события по категории для тенанта. */
    List<BotEventResponse> getEventsByCategory(String tenantSlug, Long categoryId);

    /** Возвращает событие по ID с проверкой принадлежности тенанту. */
    BotEventResponse getEventById(String tenantSlug, Long eventId);

    /** Создаёт гостевое бронирование от имени Telegram-пользователя. */
    BotBookingResponse createBooking(BotBookingRequest request);

    /** Возвращает активные бронирования Telegram-пользователя. */
    List<BotBookingResponse> getMyBookings(String tenantSlug, Long telegramId);

    /** Отменяет бронирование. Проверяет, что оно принадлежит telegramId. */
    BotBookingResponse cancelBooking(Long bookingId, Long telegramId);

    /**
     * Привязывает Telegram-чат к существующему бронированию (например, созданному
     * через веб-виджет). Используется для deep-link вида
     * {@code t.me/<bot>?start=booking_<id>}.
     *
     * <p>Если у бронирования уже есть telegramChatId — оставляет его как есть
     * (идемпотентно). Если id — другого пользователя, выбрасывает исключение.
     */
    BotBookingResponse bindTelegramChat(String tenantSlug, Long bookingId, Long telegramId);
}
