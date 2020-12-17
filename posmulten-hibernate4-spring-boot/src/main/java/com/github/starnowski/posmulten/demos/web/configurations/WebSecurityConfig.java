package com.github.starnowski.posmulten.demos.web.configurations;

import com.github.starnowski.posmulten.demos.web.filters.CorrectTenantContextFilter;
import com.github.starnowski.posmulten.demos.web.filters.CurrentTenantResolverFilter;
import com.github.starnowski.posmulten.demos.web.util.DomainResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String REALM = "posmulten_demo";

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
                .antMatchers("/app/tenants").permitAll()
                .antMatchers("/app/*/login").permitAll()
                .antMatchers("/app/**").authenticated()
                .and()
                .httpBasic().realmName(REALM).authenticationEntryPoint(basicAuthenticationEntryPoint())
                .and()
                .exceptionHandling();
        http.addFilterBefore(currentTenantResolverFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(correctTenantContextFilter(), SecurityContextPersistenceFilter.class);
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
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName(REALM);
        return entryPoint;
    }

    @Bean
    public DomainResolver domainResolver() {
        return new DomainResolver("/app/");
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
    public CorrectTenantContextFilter correctTenantContextFilter() {
        return new CorrectTenantContextFilter();
    }

    @Bean
    public CurrentTenantResolverFilter currentTenantResolverFilter() {
        return new CurrentTenantResolverFilter();
    }

}