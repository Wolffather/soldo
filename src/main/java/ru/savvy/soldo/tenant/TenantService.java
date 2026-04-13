package ru.savvy.soldo.tenant;

import ru.savvy.soldo.tenant.dto.TelegramBotSetupRequest;
import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;

public interface TenantService {

    /** Возвращает информацию о тенанте текущего пользователя. */
    TenantResponse getCurrentTenant(Long userId);

    /** Обновляет конфигурацию (лейблы, название, домен) тенанта. */
    TenantResponse updateConfig(Long userId, TenantConfigUpdateRequest request);

    /**
     * Подключает Telegram-бота тенанта: сохраняет токен, вычисляет username и
     * регистрирует webhook в Telegram API.
     */
    TenantResponse connectTelegramBot(Long userId, TelegramBotSetupRequest request);

    /** Отключает Telegram-бота: очищает токен и удаляет webhook. */
    TenantResponse disconnectTelegramBot(Long userId);
}
