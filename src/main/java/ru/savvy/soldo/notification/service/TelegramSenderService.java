package ru.savvy.soldo.notification.service;

public interface TelegramSenderService {

    void sendMessage(Long chatId, String message);
}