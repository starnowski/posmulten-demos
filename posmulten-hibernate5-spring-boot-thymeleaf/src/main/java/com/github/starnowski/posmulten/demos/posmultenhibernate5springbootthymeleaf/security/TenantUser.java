package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class TenantUser extends User {

    public static final String ROOT_TENANT_ID = "no_such_tenant";

    private String tenantId;

    public TenantUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String tenantId) {
        super(username, password, authorities);
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
