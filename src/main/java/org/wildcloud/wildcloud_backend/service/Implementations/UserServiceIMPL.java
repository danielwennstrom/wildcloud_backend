package org.wildcloud.wildcloud_backend.service.Implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.EmailTakenException;
import org.wildcloud.wildcloud_backend.exception.custom.InvalidCredentialsException;
import org.wildcloud.wildcloud_backend.exception.custom.PasswordNotValidException;
import org.wildcloud.wildcloud_backend.exception.custom.UserNotFoundException;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class UserServiceIMPL implements UserService {


    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    public UserServiceIMPL(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Flux<UserDTO> findAll() {

        return userRepository.findAll()
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findById(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + userId + " not found")))
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findByUserEmail(String email) {
        return userRepository.findByUserEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + email + " not found")))
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findByPhoneNumber(Long phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with phone number " + phoneNumber + " not found")))
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> registerUser(UserRequestDTO userRequestDTO) {

        log.info("Service method registerUser called with email: {}", userRequestDTO.getUserEmail());

        if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().length() < 5) {
            return Mono.error(new PasswordNotValidException("Password must be at least 5 characters long"));
        }


        return userRepository.existsByUserEmail(userRequestDTO.getUserEmail())
                .flatMap(exists -> {
                    log.info("Email check completed. Exists: {}", exists);
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailTakenException("Email already exists"));
                    }


                    UserInfo newUser = UserInfo.builder()
                            .userEmail(userRequestDTO.getUserEmail())
                            .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                            .firstName(userRequestDTO.getFirstName())
                            .lastName(userRequestDTO.getLastName())
                            .phoneNumber(userRequestDTO.getPhoneNumber())
                            .build();

                    log.info("Attempting to save user to database");
                    return userRepository.save(newUser);
                })
                .map(savedUser -> {
                    log.info("User saved successfully with ID: {}", savedUser.getId());
                    return this.buildUserDTO(savedUser);
                })
                .onErrorMap(e -> {
                    log.error("Error in registerUser: {}", e.getMessage(), e);
                    return e;
                });
    }

    @Override
    public Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO) {

        return userRepository.findByUserEmail(userLoginDTO.getUserEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + userLoginDTO.getUserEmail() + " not found")))
                .flatMap(userInfo -> {
                    if (!passwordEncoder.matches(userLoginDTO.getPassword(), userInfo.getPassword())) {
                        return Mono.error(new InvalidCredentialsException("Invalid credentials"));
                    }
                    return Mono.just(buildUserDTO(userInfo));
                });
    }

    @Override
    public Mono<Void> logoutUser(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + userEmail + " not found")))
                .flatMap(userInfo -> {
                    System.out.println("User " + userEmail + " logged out successfully");
                    return Mono.empty();
                });
    }

    @Override
    public Mono<UserDTO> updateUser(UserRequestDTO userRequestDTO, String userEmail) {


        return userRepository.findByUserEmail(userEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + userEmail + " not found")))
                .flatMap(userInfo -> {
                    userInfo.setUserEmail(userRequestDTO.getUserEmail());
                    userInfo.setFirstName(userRequestDTO.getFirstName());
                    userInfo.setLastName(userRequestDTO.getLastName());
                    userInfo.setPhoneNumber(userRequestDTO.getPhoneNumber());

                    return userRepository.save(userInfo)
                            .map(this::buildUserDTO);
                });
    }

    @Override
    public Mono<Void> deleteUser(String userEmail) {
        return userExistsCheck(userEmail)
                .flatMap(userInfo ->
                        userRepository.deleteById(userInfo.getId())
                );
    }


    //------------------------ Camera related methods ------------------------
    //------------------------ Camera related methods ------------------------
    //------------------------ Camera related methods ------------------------

    private UserDTO buildUserDTO(UserInfo userInfo) {
        return UserDTO.builder()
                .id(userInfo.getId())
                .userEmail(userInfo.getUserEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .build();
    }

    private Mono<UserInfo> userExistsCheck(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + id + " not found")));
    }

    private Mono<UserInfo> userExistsCheck(String email) {
        return userRepository.findByUserEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + email + " not found")));
    }
}