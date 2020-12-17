package com.github.starnowski.posmulten.demos.web.filters;

import com.github.starnowski.posmulten.demos.dto.TenantDto;
import com.github.starnowski.posmulten.demos.services.TenantService;
import com.github.starnowski.posmulten.demos.web.util.DomainResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.starnowski.posmulten.demos.util.TenantContext.setCurrentTenant;
import static com.github.starnowski.posmulten.demos.util.TenantContext.setInvalidTenant;

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
        String domain = domainResolver.resolve(httpServletRequest);
        if (domain != null) {
            TenantDto domainTenant = tenantService.findByName(domain);
            if (domainTenant != null) {
                setCurrentTenant(domainTenant.getName());
            } else {
                setInvalidTenant();
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
        setInvalidTenant();
    }

    @Override
    public void destroy() {

    }
}
