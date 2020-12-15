package com.github.starnowski.posmulten.demos.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.DispatcherType;

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/app/*/login", "/app/*/inactive-company", "/app/*/resetPasswordRequest", "/app/*/resetPassword").permitAll()
                .antMatchers("/app/*/j_spring_security_check").permitAll()
                .antMatchers("/app/*/assessments").hasAnyRole("AUDITOR", "ADMIN")
                .antMatchers("/app/*/process").hasAnyRole("AUDITOR", "ADMIN")
                .antMatchers("/app/*/admin", "/app/*/admin/**").hasRole("ADMIN")
                .antMatchers("/app/**").authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/app/*/j_spring_security_check")
                .successHandler(domainAwareSavedRequestAwareAuthenticationSuccessHandler())
                .failureHandler(domainUrlAuthenticationFailureHandler()).permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/welcome")
                .and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(domainLoginUrlAuthenticationEntryPoint(), new AntPathRequestMatcher("/app/**"));
        http.addFilterBefore(currentTenantResolverFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(correctTenantContextFilter(), SecurityContextPersistenceFilter.class);
        http.addFilterAfter(inactiveCompanyFilter(), SecurityContextPersistenceFilter.class);
        http.addFilterAfter(deletedCompanyFilter(), SecurityContextPersistenceFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public DomainLoginUrlAuthenticationEntryPoint domainLoginUrlAuthenticationEntryPoint() {
        return new DomainLoginUrlAuthenticationEntryPoint("/app/" + DomainLoginUrlAuthenticationEntryPoint.DOMAIN_URL_PART + "/login");
    }

    @Bean
    public DomainResolver domainResolver() {
        return new DomainResolver("/app/");
    }

    @Bean
    public FilterRegistrationBean inactiveCompanyFilterRegistrationBean()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(inactiveCompanyFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/app/*", "/app/**");
        registration.setOrder(4);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean deletedCompanyFilterrRegistrationBean()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(deletedCompanyFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/app/*", "/app/**");
        registration.setOrder(5);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean correctTenantContextFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(correctTenantContextFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/app/*", "/app/**");
        registration.setOrder(3);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean currentTenantResolverFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(currentTenantResolverFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/app/*", "/app/**");
        registration.setOrder(2);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean tenantFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(tenantFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/assessments");
        registration.addUrlPatterns("/assessments/*");
        registration.addUrlPatterns("/users");
        registration.addUrlPatterns("/users/*");
        registration.setOrder(1);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean optionalTenantFilterRegistrationBean()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(optionalTenantFilterRegistration());
        registration.setEnabled(true);
        registration.addUrlPatterns("/internal/domains", "/internal/domains/*");
        registration.setOrder(1);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public CorrectTenantContextFilter correctTenantContextFilter() {
        return new CorrectTenantContextFilter();
    }

    @Bean
    public InactiveCompanyFilter inactiveCompanyFilter()
    {
        return new InactiveCompanyFilter();
    }

    @Bean
    public DeletedCompanyFilter deletedCompanyFilter()
    {
        return new DeletedCompanyFilter();
    }

    @Bean
    public CurrentTenantResolverFilter currentTenantResolverFilter() {
        return new CurrentTenantResolverFilter();
    }

    @Bean
    public TenantFilter tenantFilter() {
        return new TenantFilter();
    }

    @Bean
    public DomainUrlAuthenticationFailureHandler domainUrlAuthenticationFailureHandler() {
        return new DomainUrlAuthenticationFailureHandler("/login?error", "/app/" + DomainUrlAuthenticationFailureHandler.DOMAIN_URL_PART + "/login?error", domainResolver());
    }

    @Bean
    public DomainAwareSavedRequestAwareAuthenticationSuccessHandler domainAwareSavedRequestAwareAuthenticationSuccessHandler() {
        return new DomainAwareSavedRequestAwareAuthenticationSuccessHandler("/app/" + DomainAwareSavedRequestAwareAuthenticationSuccessHandler.DOMAIN_URL_PART + "/");
    }

    @Bean
    public OptionalTenantHeaderFilter optionalTenantFilterRegistration()
    {
        return new OptionalTenantHeaderFilter();
    }
}