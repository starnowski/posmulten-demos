package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.TenantInfo;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.TenantInfoRepository;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services.SecurityServiceImpl;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID;
import static com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext.setCurrentTenant;

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
