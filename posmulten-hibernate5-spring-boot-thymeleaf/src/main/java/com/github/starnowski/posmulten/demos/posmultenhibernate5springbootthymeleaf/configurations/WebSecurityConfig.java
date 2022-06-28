package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.filters.*;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.util.DomainResolver;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainLoginUrlAuthenticationEntryPoint;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainUrlAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.DispatcherType;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.web.DomainLoginUrlAuthenticationEntryPoint.DOMAIN_URL_PART;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/posts", "/posts/**", "/users", "/users/**", "/v2/api-docs", "/swagger-ui.html")
                .permitAll()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/app/*/login").permitAll()
                .antMatchers("/app/*/j_spring_security_check").permitAll()
                .antMatchers("/app/*/posts").hasAnyRole("AUTHOR", "ADMIN")
                //TODO
                .antMatchers("/app/*/assessments").hasAnyRole("AUTHOR", "ADMIN")
                .antMatchers("/app/*/config").hasRole("ADMIN")
                .antMatchers("/app/**").authenticated()
                .and().formLogin().loginProcessingUrl("/app/*/j_spring_security_check")
                .failureHandler(domainUrlAuthenticationFailureHandler())
                .permitAll()
                .and().exceptionHandling().defaultAuthenticationEntryPointFor(domainLoginUrlAuthenticationEntryPoint(), new AntPathRequestMatcher("/app/**"));
        http.addFilterBefore(currentTenantResolverFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(correctTenantContextFilter(), SecurityContextPersistenceFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
//    }

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
        registration.addUrlPatterns("/assessments");
        registration.addUrlPatterns("/assessments/*");
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
    public TenantFilter tenantFilter() {
        return new TenantFilter();
    }

    @Bean
    public OptionalTenantFilter optionalTenantFilterRegistration() {
        return new OptionalTenantFilter();
    }

    @Bean
    public AuthenticationFailureHandler domainUrlAuthenticationFailureHandler() {
        return new DomainUrlAuthenticationFailureHandler("/login?error", "/app/" + DomainUrlAuthenticationFailureHandler.DOMAIN_URL_PART + "/login?error", domainResolver());
    }
}