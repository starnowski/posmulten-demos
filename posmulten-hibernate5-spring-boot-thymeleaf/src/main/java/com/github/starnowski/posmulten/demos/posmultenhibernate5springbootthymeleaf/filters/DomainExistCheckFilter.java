package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.TenantInfo;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.TenantInfoRepository;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The filter checks if the domain passed as part URL does exist.
 * The filter tries to find tenants based on domain.
 * If the filter does not find a tenant, it sets an http response with a 404 status.
 */
public class DomainExistCheckFilter implements Filter {

    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private DomainResolver domainResolver;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //404
        // Tenant for domain path does not exist --> return 404 status
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String domain = domainResolver.resolve(httpServletRequest);
        if (domain != null) {
            TenantInfo domainTenant = tenantInfoRepository.findByDomain(domain);
            if (domainTenant == null) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}