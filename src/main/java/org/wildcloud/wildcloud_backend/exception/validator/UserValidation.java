package org.wildcloud.wildcloud_backend.exception.validator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.*;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserValidation {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserValidation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }





    public Mono<Void> passwordValidation(String password) {
        log.info("Password validation");
        if (password == null || password.length() < 5 ) {
            log.warn("Password length less than 5");
            return Mono.error(new PasswordNotValidException("Password must be at least 5 characters long"));
        }
        log.info("Password validation successful");
        return Mono.empty();
    }


    public Mono<Void> emailValidation(String email) {

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            log.warn("Invalid email format: {}", email);
            return Mono.error(new EmailInvalidFormatException("Email format is invalid"));
        }

        return userRepository.existsByUserEmail(email)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailTakenException("Email already exists"));
                    }
                    return Mono.empty();
                });
    }


    public Mono<UserInfo> existsValidation(String email) {
        log.info("User exists validation: {}", email);
        return userRepository.findByUserEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + email + " not found")))
                .doOnSuccess(userExists -> log.info("User {} exists", email));
    }


    public Mono<UserInfo> credentialsValidation(UserInfo user, String rawPassword) {
        log.info("User credential validation: {}", user.getUserEmail());
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            log.warn("Password does not match for user: {}", user.getUserEmail());
            return Mono.error(new InvalidCredentialsException("Invalid credentials"));
        }
        log.info("User credential validation successful for user: {}", user.getUserEmail());
        return Mono.just(user);
    }
}
