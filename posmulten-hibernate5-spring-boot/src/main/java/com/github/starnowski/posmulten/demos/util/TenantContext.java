package com.github.starnowski.posmulten.demos.util;

public class TenantContext {

    public static final String INVALID_TENANT_ID = "invalid_tenant_id";
    private static ThreadLocal<String> currentTenant = ThreadLocal.withInitial(() -> INVALID_TENANT_ID);

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static void setInvalidTenant() {
        currentTenant.set(INVALID_TENANT_ID);
    }
}
