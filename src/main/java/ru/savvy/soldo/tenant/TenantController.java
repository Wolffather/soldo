package ru.savvy.soldo.tenant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.tenant.dto.TenantConfigUpdateRequest;
import ru.savvy.soldo.tenant.dto.TenantResponse;

@RestController
@RequestMapping("/admin/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<TenantResponse> getCurrent(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.getCurrentTenant(userId));
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> updateConfig(
            Authentication auth,
            @Valid @RequestBody TenantConfigUpdateRequest request) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(tenantService.updateConfig(userId, request));
    }
}
