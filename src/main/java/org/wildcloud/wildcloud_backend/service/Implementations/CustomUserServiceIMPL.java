package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class CustomUserServiceIMPL implements ReactiveUserDetailsService {


    private final UserRepository userRepository;

    public CustomUserServiceIMPL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles("USER")
                        .build());
    }
}
