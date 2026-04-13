package ru.savvy.soldo.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.tenant.model.TenantSubscription;

import java.util.Optional;

public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscription, Long> {
    Optional<TenantSubscription> findFirstByTenantIdOrderByCreatedAtDesc(Long tenantId);
}
