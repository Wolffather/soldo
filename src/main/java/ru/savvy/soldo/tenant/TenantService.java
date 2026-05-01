package ru.savvy.soldo.tenant;

import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;

public interface TenantService {

    TenantResponse getCurrentTenant(Long userId);

    TenantResponse updateConfig(Long userId, TenantConfigUpdateRequest request);
}
