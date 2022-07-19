package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

public class RequiredTenantFilter extends AbstractTenantFilter {

    @Override
    protected boolean isTenantIdRequired() {
        return true;
    }

}
