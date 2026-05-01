package ru.savvy.soldo.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;
import ru.savvy.soldo.tenant.model.Tenant;
import ru.savvy.soldo.tenant.model.TenantConfig;
import ru.savvy.soldo.tenant.model.TenantSubscription;
import ru.savvy.soldo.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantSubscriptionRepository tenantSubscriptionRepository;

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

        if (request.getName() != null && !request.getName().isBlank()) {
            tenant.setName(request.getName());
        }
        if (request.getDomain() != null) {
            tenant.setDomain(request.getDomain().isBlank() ? null : request.getDomain());
        }
        tenantRepository.save(tenant);

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
        }

        TenantSubscription sub = tenantSubscriptionRepository
                .findFirstByTenantIdOrderByCreatedAtDesc(tenantId).orElse(null);

        return toResponse(tenant, config, sub);
    }

    private Long resolveTenantId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                .getTenantId();
    }

    private TenantResponse toResponse(Tenant tenant, TenantConfig config, TenantSubscription sub) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .slug(tenant.getSlug())
                .name(tenant.getName())
                .domain(tenant.getDomain())
                .status(tenant.getStatus() != null ? tenant.getStatus().name() : null)
                .plan(sub != null && sub.getPlan() != null ? sub.getPlan().name() : "FREE")
                .maxEvents(sub != null ? sub.getMaxEvents() : null)
                .maxBookingsPerMonth(sub != null ? sub.getMaxBookingsPerMonth() : null)
                .maxAdminUsers(sub != null ? sub.getMaxAdminUsers() : 1)
                .customDomainEnabled(sub != null && sub.isCustomDomainEnabled())
                .apiAccessEnabled(sub != null && sub.isApiAccessEnabled())
                .eventLabel(config != null ? config.getEventLabel() : "Событие")
                .participantLabel(config != null ? config.getParticipantLabel() : "Участник")
                .bookingLabel(config != null ? config.getBookingLabel() : "Бронирование")
                .build();
    }
}
