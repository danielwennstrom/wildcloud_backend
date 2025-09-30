package org.wildcloud.wildcloud_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.wildcloud.wildcloud_backend.service.CustomUserService;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip authentication for public endpoints
        if (path.contains("/api/users/login") ||
            path.contains("/api/users/createUser")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return validateAndAuthenticate(token)
                    .flatMap(authentication -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                    .onErrorResume(error -> chain.filter(exchange));
        }

        return chain.filter(exchange);
    }

    private Mono<Authentication> validateAndAuthenticate(String token) {
        return Mono.just(token)
                .filterWhen(t -> jwtUtil.validateToken(t))
                .map(jwtUtil::extractEmail)
                .flatMap(userService::findByUsername)
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ));
    }
}
