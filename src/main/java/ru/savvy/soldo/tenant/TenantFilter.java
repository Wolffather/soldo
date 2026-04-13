package ru.savvy.soldo.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private static final long DEFAULT_TENANT_ID = 1L;

    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            Long tenantId = resolveTenantId(request);
            TenantContext.setCurrentTenantId(tenantId);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Long resolveTenantId(HttpServletRequest request) {
        String headerValue = request.getHeader("X-Tenant-ID");
        if (headerValue != null) {
            try {
                return Long.parseLong(headerValue.trim());
            } catch (NumberFormatException ignored) {
                // fall through to subdomain resolution
            }
        }

        String host = request.getHeader("Host");
        if (host != null) {
            String slug = extractSlug(host);
            if (slug != null) {
                return tenantRepository.findBySlug(slug)
                        .map(tenant -> tenant.getId())
                        .orElse(DEFAULT_TENANT_ID);
            }
        }

        return DEFAULT_TENANT_ID;
    }

    /**
     * Extracts the subdomain slug from a host header.
     * Example: "savvy-camp.soldo.ru" → "savvy-camp"
     * Returns null if the host has no subdomain (e.g. "soldo.ru" or "localhost").
     */
    private String extractSlug(String host) {
        // Strip port if present
        int colonIndex = host.indexOf(':');
        if (colonIndex != -1) {
            host = host.substring(0, colonIndex);
        }

        String[] parts = host.split("\\.");
        if (parts.length >= 3) {
            return parts[0];
        }

        return null;
    }
}
