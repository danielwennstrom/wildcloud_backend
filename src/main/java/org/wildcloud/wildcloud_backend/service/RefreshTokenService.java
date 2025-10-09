package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.entity.RefreshToken;
import org.wildcloud.wildcloud_backend.repository.RefreshTokenRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh.duration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public Mono<RefreshToken> createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Mono<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            return refreshTokenRepository.delete(token)
                    .then(Mono.error(new RuntimeException("Refresh token was expired. Please make a new signin request")));
        }
        return Mono.just(token);
    }

    public Mono<Void> deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }

    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
