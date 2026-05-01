package ru.savvy.soldo.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.tenant.dto.TelegramBotSetupRequest;
import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;
import ru.savvy.soldo.tenant.model.Tenant;
import ru.savvy.soldo.tenant.model.TenantConfig;
import ru.savvy.soldo.tenant.model.TenantSubscription;
import ru.savvy.soldo.user.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantSubscriptionRepository tenantSubscriptionRepository;

    @Value("${app.public-url:http://localhost:8080}")
    private String appPublicUrl;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public TenantResponse getCurrentTenant(Long userId) {
        Long tenantId = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                .getTenantId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));

        TenantConfig config = tenantConfigRepository.findById(tenantId).orElse(null);
        TenantSubscription sub = tenantSubscriptionRepository
                .findFirstByTenantIdOrderByCreatedAtDesc(tenantId).orElse(null);

        return toResponse(tenant, config, sub);
    }

    @Override
    @Transactional
    public TenantResponse updateConfig(Long userId, TenantConfigUpdateRequest request) {
        Long tenantId = resolveTenantId(userId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));

        // Update tenant name/domain
        if (request.getName() != null && !request.getName().isBlank()) {
            tenant.setName(request.getName());
        }
        if (request.getDomain() != null) {
            tenant.setDomain(request.getDomain().isBlank() ? null : request.getDomain());
        }
        tenantRepository.save(tenant);

        // Update or create config
        TenantConfig config = tenantConfigRepository.findById(tenantId).orElse(null);
        if (config == null) {
            config = TenantConfig.builder().tenantId(tenantId).build();
            config.setEventLabel(request.getEventLabel());
            config.setParticipantLabel(request.getParticipantLabel());
            config.setBookingLabel(request.getBookingLabel());
            tenantConfigRepository.save(config);
        } else {
            config.setEventLabel(request.getEventLabel());
            config.setParticipantLabel(request.getParticipantLabel());
            config.setBookingLabel(request.getBookingLabel());
            // управляемая сущность — dirty checking сохранит изменения при коммите
        }

        TenantSubscription sub = tenantSubscriptionRepository
                .findFirstByTenantIdOrderByCreatedAtDesc(tenantId).orElse(null);

        return toResponse(tenant, config, sub);
    }

    @Override
    @Transactional
    public TenantResponse connectTelegramBot(Long userId, TelegramBotSetupRequest request) {
        Long tenantId = resolveTenantId(userId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));

        if (!request.getBotToken().matches("\\d+:[A-Za-z0-9_-]{35,}")) {
            throw new IllegalOperationException(
                    "Неверный формат токена. Токен должен выглядеть как: 123456789:ABCdef...");
        }

        tenantConfigRepository.updateTelegramBot(tenantId, request.getBotToken(), generateWebhookSecret());

        TenantConfig config = tenantConfigRepository.findById(tenantId).orElse(null);
        TenantSubscription sub = tenantSubscriptionRepository
                .findFirstByTenantIdOrderByCreatedAtDesc(tenantId).orElse(null);
        return toResponse(tenant, config, sub);
    }

    @Override
    @Transactional
    public TenantResponse disconnectTelegramBot(Long userId) {
        Long tenantId = resolveTenantId(userId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Тенант не найден"));

        tenantConfigRepository.clearTelegramBot(tenantId);

        TenantConfig config = tenantConfigRepository.findById(tenantId).orElse(null);
        TenantSubscription sub = tenantSubscriptionRepository
                .findFirstByTenantIdOrderByCreatedAtDesc(tenantId).orElse(null);
        return toResponse(tenant, config, sub);
    }

    private Long resolveTenantId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                .getTenantId();
    }

    private String generateWebhookSecret() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private TenantResponse toResponse(Tenant tenant, TenantConfig config, TenantSubscription sub) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .slug(tenant.getSlug())
                .name(tenant.getName())
                .domain(tenant.getDomain())
                .status(tenant.getStatus() != null ? tenant.getStatus().name() : null)
                // Subscription
                .plan(sub != null && sub.getPlan() != null ? sub.getPlan().name() : "FREE")
                .maxEvents(sub != null ? sub.getMaxEvents() : null)
                .maxBookingsPerMonth(sub != null ? sub.getMaxBookingsPerMonth() : null)
                .maxAdminUsers(sub != null ? sub.getMaxAdminUsers() : 1)
                .customDomainEnabled(sub != null && sub.isCustomDomainEnabled())
                .apiAccessEnabled(sub != null && sub.isApiAccessEnabled())
                // Config
                .eventLabel(config != null ? config.getEventLabel() : "Событие")
                .participantLabel(config != null ? config.getParticipantLabel() : "Участник")
                .bookingLabel(config != null ? config.getBookingLabel() : "Бронирование")
                // Telegram bot
                .telegramBotEnabled(config != null
                        && config.getTelegramBotToken() != null
                        && !config.getTelegramBotToken().isBlank())
                .telegramBotUsername(config != null ? config.getTelegramBotUsername() : null)
                .telegramWebhookUrl(String.format("%s/public/bot/webhook/%s", appPublicUrl, tenant.getSlug()))
                .build();
    }
}
