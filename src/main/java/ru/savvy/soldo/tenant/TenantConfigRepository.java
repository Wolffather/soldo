package ru.savvy.soldo.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.tenant.model.TenantConfig;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {
}
