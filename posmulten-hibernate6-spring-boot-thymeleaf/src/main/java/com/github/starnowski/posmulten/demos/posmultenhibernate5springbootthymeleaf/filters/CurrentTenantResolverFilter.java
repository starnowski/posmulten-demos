package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.TenantInfo;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.TenantInfoRepository;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.SecurityServiceImpl;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID;
import static com.github.starnowski.posmulten.hibernate.common.context.CurrentTenantContext.setCurrentTenant;

/**
 * Filter that set correct tenant identifier based on domain part in URL.
 * For example if request is being set for resource "/app/some.domain/login" then filter tries to find out tenant that is related to domain "some.domain".
 * If tenant exist then the filter sets tenant identifier.
 * In other case the filter sets {@link com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID}
 */
public class CurrentTenantResolverFilter implements Filter {

    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private SecurityServiceImpl securityService;
    @Autowired
    private DomainResolver domainResolver;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //401 TODO Different class or some spring security handler
        // Not authenticated but on domain path --> redirect --> domain _ login _ page with tenant domain as resource part
        //TODO
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String domain = domainResolver.resolve(httpServletRequest);
        if (domain != null) {
            //TODO Write why setting tenant id is important here and why only this filter should do that !
            setCurrentTenant(ROOT_TENANT_ID);
            TenantInfo domainTenant = tenantInfoRepository.findByDomain(domain);
            if (domainTenant != null) {
                setCurrentTenant(domainTenant.getTenantId());
            } else {
                setCurrentTenant(ROOT_TENANT_ID);
            }

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
