package ru.savvy.soldo.notification.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.savvy.soldo.notification.service.TelegramSenderService;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.tenant.TenantConfigRepository;
import ru.savvy.soldo.tenant.TenantContext;
import ru.savvy.soldo.tenant.model.TenantConfig;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSenderServiceImpl implements TelegramSenderService {

    private final TenantConfigRepository tenantConfigRepository;

    @Value("${telegram.proxy.host:}")
    private String proxyHost;

    @Value("${telegram.proxy.port:1080}")
    private int proxyPort;

    @Value("${telegram.proxy.type:SOCKS5}")
    private String proxyType;

    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(10_000);

        if (proxyHost != null && !proxyHost.isBlank()) {
            Proxy.Type type = "HTTP".equalsIgnoreCase(proxyType) ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
            Proxy proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            factory.setProxy(proxy);
            log.info("Telegram RestTemplate настроен с {} прокси {}:{}", proxyType, proxyHost, proxyPort);
        }

        restTemplate = new RestTemplate(factory);
    }

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
        } catch (HttpClientErrorException e) {
            log.warn("Telegram API отклонил токен ({}): {}", e.getStatusCode(), e.getMessage());
            throw new IllegalOperationException(
                    "Токен бота недействителен (Telegram вернул " + e.getStatusCode() + "). "
                            + "Убедитесь, что токен скопирован корректно из @BotFather.");
        } catch (ResourceAccessException e) {
            log.error("Не удалось подключиться к Telegram API: {}", e.getMessage());
            throw new IllegalOperationException(
                    "Сервер не может подключиться к Telegram API. "
                            + "Проверьте сетевой доступ с вашего сервера до api.telegram.org "
                            + "или задайте переменную TELEGRAM_PROXY_HOST.");
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при запросе getMe к Telegram: {}", e.getMessage());
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
