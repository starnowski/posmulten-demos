package com.github.starnowski.posmulten.demos.web.filters;

import com.github.starnowski.posmulten.demos.dao.TenantRepository;
import com.github.starnowski.posmulten.demos.model.Tenant;
import com.github.starnowski.posmulten.demos.security.SecurityServiceImpl;
import com.github.starnowski.posmulten.demos.security.TenantUser;
import com.github.starnowski.posmulten.demos.web.util.DomainResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CorrectTenantContextFilter implements Filter {

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private SecurityServiceImpl securityService;
    @Autowired
    private DomainResolver domainResolver;

    @Override
    public void init(FilterConfig filterConfig) {
        log.trace("init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.trace("do");
        //403
        // Authenticated but user doesn't belongs to path domain --> 403
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        TenantUser user = securityService.findLoggedInTenantUser();
        log.trace("TenantUser = " + user);
        if (user != null) {
            String domain = domainResolver.resolve(httpServletRequest);
            log.trace("domain = " + domain);
            if (domain != null) {
                Tenant domainTenant = tenantRepository.getOne(domain);
                if (domainTenant != null && !domainTenant.getName().equals(user.getTenantId())) {
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
        log.trace("destroy");
    }
}