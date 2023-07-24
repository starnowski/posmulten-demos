package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class DomainUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public static final String DOMAIN_URL_PART = "{tenantDomain}";
    public static final String DOMAIN_URL_PART_MATCH_GROUP = "\\{tenantDomain}";

    private String domainDefaultFailureUrl;

    private DomainResolver domainResolver;

    public DomainUrlAuthenticationFailureHandler(String defaultFailureUrl, String domainDefaultFailureUrl, DomainResolver domainResolver) {
        super(defaultFailureUrl);
        //TODO Validate if not null
        this.domainDefaultFailureUrl = domainDefaultFailureUrl;
        this.domainResolver = domainResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String domain = domainResolver.resolve(request);
        if (domain != null) {
            String redirectUrl = domainDefaultFailureUrl.replaceAll(DOMAIN_URL_PART_MATCH_GROUP, domain);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}