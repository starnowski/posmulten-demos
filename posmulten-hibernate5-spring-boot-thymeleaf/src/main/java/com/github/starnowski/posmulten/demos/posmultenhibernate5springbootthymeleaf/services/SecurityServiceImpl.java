package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl {

    public TenantUser findLoggedInTenantUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof TenantUser) {
            return (TenantUser) principal;
        }
        return null;
    }
}