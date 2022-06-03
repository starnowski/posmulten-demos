package com.github.starnowski.posmulten.demos.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class TenantUser extends User {
    private final String tenantId;
    private final UUID userId;

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
