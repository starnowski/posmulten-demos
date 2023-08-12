package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.util;


import jakarta.servlet.http.HttpServletRequest;

public class DomainResolver {

    public DomainResolver(String resourceNonDomainPrefix) {
        this.resourceNonDomainPrefix = resourceNonDomainPrefix;
    }

    private final String resourceNonDomainPrefix;

    public String resolve(String contextPath){
        if (contextPath == null || contextPath.trim().isEmpty() || !contextPath.startsWith(resourceNonDomainPrefix)) {
            return null;
        }
        String contextPathWithoutResourceDomainPrefix = contextPath.substring(resourceNonDomainPrefix.length());
        String[] contextParts = contextPathWithoutResourceDomainPrefix.split("/");
        return contextParts[0];
    }

    public String resolve(HttpServletRequest request)
    {
        return resolve(request.getServletPath());
    }
}
