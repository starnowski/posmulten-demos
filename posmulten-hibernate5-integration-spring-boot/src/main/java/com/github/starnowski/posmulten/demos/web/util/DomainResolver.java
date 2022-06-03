package com.github.starnowski.posmulten.demos.web.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class DomainResolver {

    public DomainResolver(String resourceNonDomainPrefix) {
        this.resourceNonDomainPrefix = resourceNonDomainPrefix;
    }

    private final String resourceNonDomainPrefix;

    public String resolve(String contextPath) {
        if (contextPath == null || contextPath.trim().isEmpty() || !contextPath.startsWith(resourceNonDomainPrefix)) {
            return null;
        }
        String contextPathWithoutResourceDomainPrefix = contextPath.substring(resourceNonDomainPrefix.length());
        String[] contextParts = contextPathWithoutResourceDomainPrefix.split("/");
        log.trace("resolved = " + contextParts[0]);
        return contextParts[0];
    }

    public String resolve(HttpServletRequest request) {
        log.trace("servletPath = " + request.getServletPath());
        return resolve(request.getServletPath());
    }
}
