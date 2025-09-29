package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
        return userRepository.findByUserEmail(username)
                .map(user -> User.withUsername(user.getUserEmail())
                        .password(user.getPassword())
                        .roles("USER")
                        .build());
    }
}
