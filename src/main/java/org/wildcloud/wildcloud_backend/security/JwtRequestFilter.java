package org.wildcloud.wildcloud_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.wildcloud.wildcloud_backend.service.CustomUserService;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.validateToken(token)
                    .filter(valid -> valid)
                    .flatMap(valid -> {
                        String email = jwtUtil.extractEmail(token);
                        return userService.findByUsername(email)
                                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                ));
                    })
                    .flatMap(auth -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                    .switchIfEmpty(chain.filter(exchange));
        }
        return chain.filter(exchange);
    }
}
