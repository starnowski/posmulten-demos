package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.web;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util.DomainResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class DomainLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    public static final String DOMAIN_URL_PART = "{tenantDomain}";
    public static final String DOMAIN_URL_PART_MATCH_GROUP = "\\{tenantDomain}";
    @Autowired
    private DomainResolver domainResolver;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final String logoutUrlPattern;

    public DomainLogoutSuccessHandler(String logoutUrlPattern) {
        this.logoutUrlPattern = logoutUrlPattern;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String domain = domainResolver.resolve(request);
        if (domain != null) {
            return logoutUrlPattern.replaceAll(DOMAIN_URL_PART_MATCH_GROUP, domain);
        }
        return "";
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        this.handle(request, response, authentication);
    }
}
