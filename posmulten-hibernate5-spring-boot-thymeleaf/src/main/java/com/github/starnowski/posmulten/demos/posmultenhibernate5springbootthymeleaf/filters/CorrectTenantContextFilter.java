package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorrectTenantContextFilter  implements Filter {

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
        //403
        // Authenticated but user doesn't belongs to path domain --> 403
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        TenantUser user = securityService.findLoggedInTenantUser();
        if (user != null) {
            String domain = domainResolver.resolve(httpServletRequest);
            if (domain != null) {
                TenantInfo domainTenant = tenantInfoRepository.findByDomain(domain);
                if (domainTenant != null && !domainTenant.getTenantId().equals(user.getTenantId())) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return;
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}