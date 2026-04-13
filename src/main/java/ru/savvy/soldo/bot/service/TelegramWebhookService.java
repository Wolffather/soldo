package ru.savvy.soldo.bot.service;

import java.util.Map;

public interface TelegramWebhookService {

    /**
     * Обрабатывает входящее обновление от Telegram для конкретного тенанта.
     *
     * @param tenantSlug      slug тенанта (берётся из URL webhook)
     * @param secretFromHeader значение заголовка X-Telegram-Bot-Api-Secret-Token
     * @param update          JSON-тело обновления от Telegram
     */
    void handleUpdate(String tenantSlug, String secretFromHeader, Map<String, Object> update);
}
