package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.web;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util.DomainResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;


public class DomainLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public static final String DOMAIN_URL_PART = "{tenantDomain}";
    public static final String DOMAIN_URL_PART_MATCH_GROUP = "\\{tenantDomain}";

    @Autowired
    private DomainResolver domainResolver;

    public DomainLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        //TODO validate if loginFormUrl has "DOMAIN_URL_PART_MATCH_GROUP" group
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String domain = domainResolver.resolve(request);
        if (domain != null) {
            return getLoginFormUrl().replaceAll(DOMAIN_URL_PART_MATCH_GROUP, domain);
        }
        return "";
    }
}
