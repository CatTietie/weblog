package com.quanxiaoha.weblog.common.context;

public class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> IGNORE_TENANT = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setIgnore(boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    public static boolean isIgnore() {
        Boolean ignore = IGNORE_TENANT.get();
        return ignore != null && ignore;
    }

    public static void clear() {
        TENANT_ID.remove();
        IGNORE_TENANT.remove();
    }
}
