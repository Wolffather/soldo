package ru.savvy.soldo.bot.service.dialog;

/**
 * Шаги диалога бронирования через Telegram-бота.
 */
public enum BotDialogStep {
    IDLE,
    CHOOSING_CATEGORY,
    CHOOSING_EVENT,
    ENTERING_NAME,
    ENTERING_PHONE,
    CONFIRMING
}
