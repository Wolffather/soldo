package ru.savvy.soldo.bot.service.dialog;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простое in-memory хранилище состояний диалога Telegram-бота.
 * Ключ — композитный (tenantId, chatId), чтобы разные тенанты
 * с совпадающими chatId не мешали друг другу.
 *
 * <p>Для одно-инстансового деплоя этого достаточно. Для кластера
 * понадобится внешнее хранилище (Redis, БД).
 */
@Service
public class BotDialogStateService {

    private final Map<String, BotDialogState> states = new ConcurrentHashMap<>();

    public BotDialogState getOrCreate(Long tenantId, Long chatId) {
        return states.computeIfAbsent(key(tenantId, chatId), k -> new BotDialogState());
    }

    public void clear(Long tenantId, Long chatId) {
        BotDialogState state = states.get(key(tenantId, chatId));
        if (state != null) {
            state.reset();
        }
    }

    public void remove(Long tenantId, Long chatId) {
        states.remove(key(tenantId, chatId));
    }

    private String key(Long tenantId, Long chatId) {
        return tenantId + ":" + chatId;
    }
}
