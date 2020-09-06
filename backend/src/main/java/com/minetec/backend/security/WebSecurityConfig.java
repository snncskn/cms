package com.minetec.backend.security;

import com.minetec.backend.security.filter.AuthenticationFilter;
import com.minetec.backend.security.filter.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationFilter authenticationFilter;
    private final AuthorizationFilter authorizationFilter;

    @Bean(name = "authenticationFilterRegistration")
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration(
        final AuthenticationFilter filter
    ) {

        final FilterRegistrationBean<AuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);

        return filterRegistrationBean;
    }

    @Bean(name = "authorizationFilterRegistration")
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration(
        final AuthorizationFilter filter
    ) {

        final FilterRegistrationBean<AuthorizationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);

        return filterRegistrationBean;
    }

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {

        http.httpBasic().disable();

        http.csrf().disable();

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
            .addFilterBefore(authenticationFilter, LogoutFilter.class)
            .addFilterBefore(authorizationFilter, LogoutFilter.class);
    }
}

