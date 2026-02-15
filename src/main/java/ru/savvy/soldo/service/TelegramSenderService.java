package ru.savvy.soldo.service;

public interface TelegramSenderService {

    void sendMessage(Long chatId, String message);
}