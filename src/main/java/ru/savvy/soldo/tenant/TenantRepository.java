package ru.savvy.soldo.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.tenant.model.Tenant;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findBySlug(String slug);
    Optional<Tenant> findByDomain(String domain);
}
