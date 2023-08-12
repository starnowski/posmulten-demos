package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.TenantInfo;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.repositories.TenantInfoRepository;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util.DomainResolver;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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