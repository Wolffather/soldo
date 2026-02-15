package ru.savvy.soldo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.savvy.soldo.service.TelegramSenderService;

import java.util.Map;

@Slf4j
@Service
public class TelegramSenderServiceImpl implements TelegramSenderService {

    @Value("${telegram.bot.token:}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendMessage(Long chatId, String message) {
        if (botToken.isBlank()) {
            log.warn("Telegram bot token не настроен, сообщение не отправлено: {}", message);
            return;
        }

        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        try {
            restTemplate.postForObject(url, Map.of(
                    "chat_id", chatId,
                    "text", message,
                    "parse_mode", "HTML"
            ), String.class);

            log.info("Telegram сообщение отправлено в чат {}", chatId);
        } catch (Exception e) {
            log.error("Ошибка отправки Telegram сообщения в чат {}: {}", chatId, e.getMessage());
            throw e;
        }
    }
}