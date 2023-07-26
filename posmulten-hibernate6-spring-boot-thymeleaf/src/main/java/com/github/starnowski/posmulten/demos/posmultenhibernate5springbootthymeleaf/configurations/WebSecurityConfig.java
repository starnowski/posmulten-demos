package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters.*;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainAwareSavedRequestAwareAuthenticationSuccessHandler;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainLoginUrlAuthenticationEntryPoint;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainLogoutSuccessHandler;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainUrlAuthenticationFailureHandler;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainLoginUrlAuthenticationEntryPoint.DOMAIN_URL_PART;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .authorizeRequests().requestMatchers("/posts", "/posts/**", "/users", "/users/**", "/v2/api-docs", "/swagger-ui.html")
                .permitAll()
                .and()
                .authorizeRequests()
                .requestMatchers("/app/*/login").permitAll()
                .requestMatchers("/app/*/logout").permitAll()
                .requestMatchers("/app/*/j_spring_security_check").permitAll()
                .requestMatchers("/app/*/posts", "/app/*/posts/").hasAnyRole("AUTHOR", "ADMIN")//TODO Change to all authenticated
                .requestMatchers("/app/*/config", "/app/*/config/").hasRole("ADMIN") // TODO No such resource yet
                .requestMatchers("/app/*/users", "/app/*/users/").hasRole("ADMIN")
                .requestMatchers("/app/**").authenticated()
                .and()
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginProcessingUrl("/app/*/j_spring_security_check")
                        .successHandler(domainAwareSavedRequestAwareAuthenticationSuccessHandler())
                        .failureHandler(domainUrlAuthenticationFailureHandler()).permitAll())
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutUrl("/app/*/logout")
                        .logoutSuccessHandler(domainLogoutSuccessHandler()))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.defaultAuthenticationEntryPointFor(domainLoginUrlAuthenticationEntryPoint(), new AntPathRequestMatcher("/app/**")))
        ;
        http.addFilterBefore(currentTenantResolverFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(correctTenantContextFilter(), SecurityContextPersistenceFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint domainLoginUrlAuthenticationEntryPoint() {
        return new DomainLoginUrlAuthenticationEntryPoint("/app/" + DOMAIN_URL_PART + "/login");
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
    public FilterRegistrationBean domainExistCheckFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(domainExistCheckFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/app/*", "/app/**");
        registration.setOrder(1);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean tenantFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(tenantFilter());
        registration.setEnabled(true);
        registration.addUrlPatterns("/posts");
        registration.addUrlPatterns("/posts/*");
        registration.addUrlPatterns("/users");
        registration.addUrlPatterns("/users/*");
        registration.setOrder(1);
        registration.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.REQUEST);
        return registration;
    }

    @Bean
    public FilterRegistrationBean optionalTenantFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(optionalTenantFilterRegistration());
        registration.setEnabled(true);
        registration.addUrlPatterns("/internal/domains");
        registration.setOrder(1);
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

    @Bean
    public DomainExistCheckFilter domainExistCheckFilter() {
        return new DomainExistCheckFilter();
    }

    @Bean
    public RequiredTenantFilter tenantFilter() {
        return new RequiredTenantFilter();
    }

    @Bean
    public OptionalTenantFilter optionalTenantFilterRegistration() {
        return new OptionalTenantFilter();
    }

    @Bean
    public AuthenticationFailureHandler domainUrlAuthenticationFailureHandler() {
        return new DomainUrlAuthenticationFailureHandler("/login?error", "/app/" + DomainUrlAuthenticationFailureHandler.DOMAIN_URL_PART + "/login?error", domainResolver());
    }

    @Bean
    public DomainAwareSavedRequestAwareAuthenticationSuccessHandler domainAwareSavedRequestAwareAuthenticationSuccessHandler() {
        return new DomainAwareSavedRequestAwareAuthenticationSuccessHandler("/app/" + DomainAwareSavedRequestAwareAuthenticationSuccessHandler.DOMAIN_URL_PART + "/");
    }

    @Bean
    public DomainLogoutSuccessHandler domainLogoutSuccessHandler() {
        return new DomainLogoutSuccessHandler("/app/" + DomainLogoutSuccessHandler.DOMAIN_URL_PART + "/login");
    }
}