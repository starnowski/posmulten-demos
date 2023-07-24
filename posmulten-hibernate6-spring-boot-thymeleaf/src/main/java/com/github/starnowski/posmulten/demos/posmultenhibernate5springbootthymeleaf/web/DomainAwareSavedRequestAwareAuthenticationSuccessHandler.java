package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DomainAwareSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String DOMAIN_URL_PART = "{tenantDomain}";
    public static final String DOMAIN_URL_PART_MATCH_GROUP = "\\{tenantDomain}";

    public static final String DEFAULT_NON_DOMAIN_URL_TARGET = "/";

    private String nonDomainUrlTarget = DEFAULT_NON_DOMAIN_URL_TARGET;

    private final String domainAwareDefaultUrlPattern;

    @Autowired
    private DomainResolver domainResolver;

    public DomainAwareSavedRequestAwareAuthenticationSuccessHandler(String domainAwareDefaultUrlPattern)
    {
        this.domainAwareDefaultUrlPattern = domainAwareDefaultUrlPattern;
        setDefaultTargetUrl(DEFAULT_NON_DOMAIN_URL_TARGET);
        setAlwaysUseDefaultTargetUrl(false);
        Assert.notNull(domainAwareDefaultUrlPattern, "domainAwareDefaultUrlPattern can not be null");
        if (!domainAwareDefaultUrlPattern.contains(DOMAIN_URL_PART))
        {
            throw new IllegalArgumentException("url '" + domainAwareDefaultUrlPattern + "' does not contains group '" + DOMAIN_URL_PART + "'");
        }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String resolvedTargetUrl = super.determineTargetUrl(request, response);
        if (nonDomainUrlTarget.equals(resolvedTargetUrl)) {
            String domain = domainResolver.resolve(request);
            if (domain != null) {
                return domainAwareDefaultUrlPattern.replaceAll(DOMAIN_URL_PART_MATCH_GROUP, domain);
            }
        }

        return resolvedTargetUrl;
    }
}
