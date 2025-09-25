package org.wildcloud.wildcloud_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/users/createUser" , "/api/users/login", "/api/users/getAllUsers")  /// Ta bort getAllUsers senare efter testning.
                                .permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(authLoggingFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();

    }

    public WebFilter authLoggingFilter() {

        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();
            logger.debug("Working on request: {} {}", method, path);

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                logger.debug("Authorization header present");
            } else {

                logger.debug("No Authorization header present: {}", authHeader);
            }
            return chain.filter(exchange);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

