package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model.TenantInfo;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories.TenantInfoRepository;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.SecurityServiceImpl;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID;
import static com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext.setCurrentTenant;

/**
 * Filter that checks if authenticated user wants to get access to domain to which he belongs.
 * If user domain and domain which he request is different that filter set http response with status 403
 * For example when user log in in for domain "start1" - /app/start1/login
 * - when user tries to get resource "/app/start1/posts" then filter does not do anything
 * - when user tries to get resource "/app/stop2/posts" (different domain) then filter set response http response with status 403
 *
 */
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
                setCurrentTenant(ROOT_TENANT_ID);
                TenantInfo domainTenant = tenantInfoRepository.findByDomain(domain);
                if (domainTenant != null && !domainTenant.getTenantId().equals(user.getTenantId())) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return;
                } else {
                    setCurrentTenant(domainTenant.getTenantId());
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}