package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class TenantUser extends User {

    public static final String ROOT_TENANT_ID = "no_such_tenant";

    private String tenantId;

    private UUID userId;

    public TenantUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String tenantId, UUID userId) {
        super(username, password, authorities);
        this.tenantId = tenantId;
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public UUID getUserId() {
        return userId;
    }
}
