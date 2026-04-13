package ru.savvy.soldo.notification.service;

import java.util.List;
import java.util.Map;

public interface TelegramSenderService {

    /**
     * Отправляет сообщение в Telegram используя бот текущего тенанта
     * (tenantId берётся из {@code TenantContext}).
     */
    void sendMessage(Long chatId, String message);

    /**
     * Отправляет сообщение через бот конкретного тенанта.
     * Используется в фоновых задачах без tenant-контекста.
     */
    void sendMessage(Long tenantId, Long chatId, String message);

    /**
     * Отправляет сообщение с inline-клавиатурой. {@code keyboard} —
     * список рядов, каждый ряд — список кнопок; каждая кнопка —
     * карта с ключами {@code text} и {@code callback_data}
     * (или {@code url}).
     */
    void sendMessage(Long tenantId, Long chatId, String message,
                     List<List<Map<String, Object>>> keyboard);

    /**
     * Подтверждает обработку callback query (убирает "часики" на кнопке).
     */
    void answerCallbackQuery(Long tenantId, String callbackQueryId, String text);

    /**
     * Регистрирует webhook бота тенанта в Telegram API.
     * Возвращает true, если Telegram подтвердил регистрацию.
     */
    boolean registerWebhook(Long tenantId, String webhookUrl);

    /**
     * Удаляет webhook бота тенанта в Telegram API.
     */
    boolean deleteWebhook(Long tenantId);

    /**
     * Возвращает username бота тенанта (без @). Null, если токен не настроен
     * или запрос к Telegram API не удался.
     */
    String fetchBotUsername(String botToken);
}
