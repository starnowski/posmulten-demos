package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.filters;

public class OptionalTenantFilter extends AbstractTenantFilter {
    @Override
    protected boolean isTenantIdRequired() {
        return false;
    }
}
