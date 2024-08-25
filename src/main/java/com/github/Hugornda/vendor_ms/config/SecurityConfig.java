package com.github.Hugornda.vendor_ms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USERNAME = "admin";
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.admin-password}")
    private String adminPassword;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        String encode = passwordEncoder().encode(adminPassword);
        log.info(encode);
        UserDetails user = User.builder()
                .username(USERNAME)
                .password(encode)
                .roles(ADMIN_ROLE)
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable() // Disable CSRF
            .authorizeExchange()
            .pathMatchers("/graphql").authenticated() // Auth only required for the graphql requests
            .anyExchange().permitAll() // Allow other paths
            .and()
            .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
