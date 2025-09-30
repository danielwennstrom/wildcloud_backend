package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.wildcloud.wildcloud_backend.entity.RefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, Long> {
    Mono<RefreshToken> findByToken(String token);
    Mono<RefreshToken> findByUserId(Long userId);
    Mono<Void> deleteByUserId(Long userId);
}
