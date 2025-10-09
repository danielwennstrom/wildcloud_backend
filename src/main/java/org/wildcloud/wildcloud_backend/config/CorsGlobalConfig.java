package org.wildcloud.wildcloud_backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsGlobalConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);          // allow cookies/auth headers
        corsConfig.addAllowedOrigin("http://localhost:8081"); // your frontend origin
        corsConfig.addAllowedHeader("*");              // allow all headers
        corsConfig.addAllowedMethod("*");              // allow GET, POST, PUT, DELETE, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // apply to all endpoints

        return new CorsWebFilter(source);
    }
}