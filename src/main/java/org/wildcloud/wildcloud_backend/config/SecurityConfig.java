package org.wildcloud.wildcloud_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: ändra permits när authorization är på plats
        http.csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/upload/**"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/upload/**").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}