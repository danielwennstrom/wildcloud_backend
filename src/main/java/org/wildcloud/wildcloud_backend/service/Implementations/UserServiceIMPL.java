package org.wildcloud.wildcloud_backend.service.Implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.EmailTakenException;
import org.wildcloud.wildcloud_backend.exception.custom.InvalidCredentialsException;
import org.wildcloud.wildcloud_backend.exception.custom.UserNotFoundException;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceIMPL implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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

        return userRepository.existsByUserEmail(userRequestDTO.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailTakenException("User with email " + userRequestDTO.getEmail() + " already exists"));
                    }
                    UserInfo newUser = UserInfo.builder()
                            .userEmail(userRequestDTO.getEmail())
                            .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                            .firstName(userRequestDTO.getFirstName())
                            .lastName(userRequestDTO.getLastName())
                            .phoneNumber(userRequestDTO.getPhoneNumber())
                            .build();
                    return userRepository.save(newUser)
                            .map(this::buildUserDTO)
                            .doOnError(e ->System.err.println("Error saving user: " + e.getMessage()));
                });

    }

    @Override
    public Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO) {

        return userRepository.findByUserEmail(userLoginDTO.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email " + userLoginDTO.getEmail() + " not found")))
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
    public Mono<UserDTO> updateUser(UserRequestDTO userRequestDTO, Long userId) {


        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + userId + "not found")))
                .flatMap(userInfo -> {
                    if (!userRequestDTO.getEmail().equals(userInfo.getUserEmail())) {
                        return userRepository.findByUserEmail(userRequestDTO.getEmail())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(new EmailTakenException("User with email " + userRequestDTO.getEmail() + " already exists"));
                                    }
                                    userInfo.setUserEmail(userRequestDTO.getEmail());
                                    return Mono.just(userInfo);
                                });
                    }
                    return Mono.just(userInfo);
                })
                .map(userInfo -> {
                    if (!passwordEncoder.matches(userRequestDTO.getPassword(), userInfo.getPassword())) {
                        userInfo.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
                    }
                    userInfo.setFirstName(userRequestDTO.getFirstName());
                    userInfo.setLastName(userRequestDTO.getLastName());
                    userInfo.setPhoneNumber(userRequestDTO.getPhoneNumber());
                    return userInfo;
                })
                .flatMap(userRepository::save)
                .map(this::buildUserDTO);

    }

    @Override
    public Mono<Void> deleteUser(Long userId) {
        return userExistsCheck(userId)
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
                .email(userInfo.getUserEmail())
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