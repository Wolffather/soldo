package ru.savvy.soldo.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.savvy.soldo.notification.service.TelegramSenderService;
import ru.savvy.soldo.tenant.TenantConfigRepository;
import ru.savvy.soldo.tenant.TenantContext;
import ru.savvy.soldo.tenant.model.TenantConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSenderServiceImpl implements TelegramSenderService {

    private final TenantConfigRepository tenantConfigRepository;

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void sendMessage(Long chatId, String message) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            log.warn("sendMessage: нет текущего tenantId, сообщение не отправлено: {}", message);
            return;
        }
        sendMessage(tenantId, chatId, message);
    }

    @Override
    public void sendMessage(Long tenantId, Long chatId, String message) {
        sendMessage(tenantId, chatId, message, null);
    }

    @Override
    public void sendMessage(Long tenantId, Long chatId, String message,
                            List<List<Map<String, Object>>> keyboard) {
        String token = resolveToken(tenantId);
        if (token == null) {
            log.warn("Telegram бот не настроен для тенанта {}, сообщение не отправлено", tenantId);
            return;
        }

        String url = String.format("https://api.telegram.org/bot%s/sendMessage", token);

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "HTML");
        if (keyboard != null && !keyboard.isEmpty()) {
            body.put("reply_markup", Map.of("inline_keyboard", keyboard));
        }

        try {
            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram сообщение отправлено (tenant={}, chat={})", tenantId, chatId);
        } catch (Exception e) {
            log.error("Ошибка отправки Telegram сообщения (tenant={}, chat={}): {}",
                    tenantId, chatId, e.getMessage());
        }
    }

    @Override
    public void answerCallbackQuery(Long tenantId, String callbackQueryId, String text) {
        String token = resolveToken(tenantId);
        if (token == null || callbackQueryId == null) return;

        String url = String.format("https://api.telegram.org/bot%s/answerCallbackQuery", token);
        Map<String, Object> body = new HashMap<>();
        body.put("callback_query_id", callbackQueryId);
        if (text != null && !text.isBlank()) {
            body.put("text", text);
        }
        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            log.warn("Ошибка answerCallbackQuery (tenant={}): {}", tenantId, e.getMessage());
        }
    }

    @Override
    public boolean registerWebhook(Long tenantId, String webhookUrl) {
        String token = resolveToken(tenantId);
        if (token == null) {
            log.warn("registerWebhook: токен бота не настроен для тенанта {}", tenantId);
            return false;
        }

        TenantConfig cfg = tenantConfigRepository.findById(tenantId).orElse(null);
        String secret = cfg != null ? cfg.getTelegramWebhookSecret() : null;

        String url = String.format("https://api.telegram.org/bot%s/setWebhook", token);
        Map<String, Object> body = secret != null
                ? Map.of("url", webhookUrl, "secret_token", secret)
                : Map.of("url", webhookUrl);

        try {
            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram webhook зарегистрирован для тенанта {}: {}", tenantId, webhookUrl);
            return true;
        } catch (Exception e) {
            log.error("Ошибка регистрации Telegram webhook для тенанта {}: {}",
                    tenantId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteWebhook(Long tenantId) {
        String token = resolveToken(tenantId);
        if (token == null) return false;
        String url = String.format("https://api.telegram.org/bot%s/deleteWebhook", token);
        try {
            restTemplate.postForObject(url, Map.of(), String.class);
            log.info("Telegram webhook удалён для тенанта {}", tenantId);
            return true;
        } catch (Exception e) {
            log.error("Ошибка удаления Telegram webhook для тенанта {}: {}", tenantId, e.getMessage());
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String fetchBotUsername(String botToken) {
        if (botToken == null || botToken.isBlank()) return null;
        String url = String.format("https://api.telegram.org/bot%s/getMe", botToken);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return null;
            Object ok = response.get("ok");
            if (!(ok instanceof Boolean) || !((Boolean) ok)) return null;
            Object result = response.get("result");
            if (result instanceof Map<?, ?> map) {
                Object username = map.get("username");
                return username != null ? username.toString() : null;
            }
        } catch (Exception e) {
            log.error("Ошибка запроса getMe к Telegram: {}", e.getMessage());
        }
        return null;
    }

    private String resolveToken(Long tenantId) {
        if (tenantId == null) return null;
        TenantConfig cfg = tenantConfigRepository.findById(tenantId).orElse(null);
        if (cfg == null) return null;
        String token = cfg.getTelegramBotToken();
        return (token == null || token.isBlank()) ? null : token;
    }
}
