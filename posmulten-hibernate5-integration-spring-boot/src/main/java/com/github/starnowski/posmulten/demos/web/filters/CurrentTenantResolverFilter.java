package com.github.starnowski.posmulten.demos.web.filters;

import com.github.starnowski.posmulten.demos.dto.TenantDto;
import com.github.starnowski.posmulten.demos.services.TenantService;
import com.github.starnowski.posmulten.demos.web.util.DomainResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.starnowski.posmulten.demos.util.TenantContext.INVALID_TENANT_ID;
import static com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext.setCurrentTenant;


@Slf4j
public class CurrentTenantResolverFilter implements Filter {

    @Autowired
    private TenantService tenantService;
    @Autowired
    private DomainResolver domainResolver;

    public static final String DOMAIN = "domain";

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        setCurrentTenant(INVALID_TENANT_ID);
        String domain = domainResolver.resolve(httpServletRequest);
        if (domain != null) {
            TenantDto domainTenant = tenantService.findByName(domain);
            if (domainTenant != null) {
                setCurrentTenant(domainTenant.getName());
            } else {
                setCurrentTenant(INVALID_TENANT_ID);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
        setCurrentTenant(INVALID_TENANT_ID);
    }

    @Override
    public void destroy() {

    }
}
