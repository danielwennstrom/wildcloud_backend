package org.wildcloud.wildcloud_backend.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class CustomUserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUserEmail(username)
                .map(user -> User.withUsername(user.getUserEmail())
                        .password(user.getPassword())
                        .roles("USER")
                        .build());
    }
}
