package ru.savvy.soldo.tenant;

public final class TenantContext {
    private static final InheritableThreadLocal<Long> CURRENT = new InheritableThreadLocal<>();

    private TenantContext() {}

    public static Long getCurrentTenantId() {
        return CURRENT.get();
    }

    public static void setCurrentTenantId(Long tenantId) {
        CURRENT.set(tenantId);
    }

    public static void clear() {
        CURRENT.remove();
    }
}
