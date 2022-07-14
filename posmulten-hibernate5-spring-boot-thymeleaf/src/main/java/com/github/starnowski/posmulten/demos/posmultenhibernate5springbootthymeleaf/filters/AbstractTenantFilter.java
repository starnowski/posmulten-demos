package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters;

import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID;
import static com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext.setCurrentTenant;

public abstract class AbstractTenantFilter implements Filter {

    public static final String TENANT_HEADER = "X-TenantID";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String tenantHeader = request.getHeader(TENANT_HEADER);
        if (tenantHeader != null && !tenantHeader.isEmpty()) {
            //TODO Do not set PRIVILEGED_TENANT_ID, ever
            setCurrentTenant(tenantHeader);
        } else if (isTenantIdRequired()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"No tenant supplied\"}");
            response.getWriter().flush();
            return;
        } else {
            //TODO add unit tests
            setCurrentTenant(ROOT_TENANT_ID);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    abstract protected boolean isTenantIdRequired();

    @Override
    public void destroy() {
    }
}