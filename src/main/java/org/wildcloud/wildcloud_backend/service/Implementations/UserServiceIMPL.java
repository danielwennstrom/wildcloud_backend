package org.wildcloud.wildcloud_backend.service.Implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.RelationEntity.UserCamera;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.UserNotFoundException;
import org.wildcloud.wildcloud_backend.exception.validator.UserValidation;
import org.wildcloud.wildcloud_backend.repository.RelationRepository.UserCameraRepository;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.CameraService;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class UserServiceIMPL implements UserService {


    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final PasswordEncoder passwordEncoder;
    private final CameraService cameraService;
    private final UserCameraRepository userCameraRepository;

    public UserServiceIMPL(UserRepository userRepository, UserValidation userValidation, PasswordEncoder passwordEncoder, CameraService cameraService, UserCameraRepository userCameraRepository) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
        this.passwordEncoder = passwordEncoder;
        this.cameraService = cameraService;
        this.userCameraRepository = userCameraRepository;
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
    public Flux<UserDTO> findUsersByCameraId(Long cameraId) {
        return cameraService.findById(cameraId)
                .flatMapMany(cameraInfo -> userCameraRepository.findByCameraId(cameraId))
                .flatMap(userCamera -> userRepository.findById(userCamera.getUserId()))
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

        return userValidation.passwordValidation(userRequestDTO.getPassword())
                .then(userValidation.emailValidation(userRequestDTO.getUserEmail()))
                .then(Mono.defer(() -> {

                    log.info("Creating new user entity");

                    UserInfo newUser = UserInfo.builder()
                            .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                            .userEmail(userRequestDTO.getUserEmail())
                            .firstName(userRequestDTO.getFirstName())
                            .lastName(userRequestDTO.getLastName())
                            .phoneNumber(userRequestDTO.getPhoneNumber())
                            .build();

                    log.info("Attempting to save user to database");
                    return userRepository.save(newUser);

                }))
                        .doOnSuccess(savedUser -> log.info("User registered successfully with id: {}", savedUser.getId()))
                .map(this::buildUserDTO)
                        .onErrorMap(error -> {
                            log.error("Error in user user registration: {}", error.getMessage(), error);
                            return error;
                        });
    }

    @Override
    public Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO) {
        log.info("Service method loginUser called with email: {}", userLoginDTO.getUserEmail());

        return userValidation.existsValidation(userLoginDTO.getUserEmail())
                .flatMap(userInfo -> userValidation.credentialsValidation(userInfo, userLoginDTO.getPassword()))
                .map(this::buildUserDTO)
                .doOnSuccess(userDTO ->  log.info("User login successfully with email: {}", userDTO.getUserEmail()))
                .onErrorMap(error -> {
                    log.error("Error in user user login: {}", error.getMessage(), error);
                    return error;
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
    @Override
    public Mono<Void> assignCameraToUser(Long cameraId, String userEmail) {
        return userExistsCheck(userEmail)
                .flatMap(userInfo -> cameraService.findById(cameraId)
                        .flatMap(cameraInfo -> userCameraRepository.findByUserIdAndCameraId(userInfo.getId(),cameraId)
                                .flatMap(existingRelation -> Mono.error(new RuntimeException("Camera already assigned to user")))
                                .switchIfEmpty(Mono.defer(() -> {
                                    UserCamera userCamera = UserCamera.builder()
                                            .userId(userInfo.getId())
                                            .cameraId(cameraId)
                                            .build();
                                    return userCameraRepository.save(userCamera);
                                }))
                        )
                ).then();
    }

    @Override
    public Flux<CameraDTO> getUserCameras(String userEmail) {
        return userExistsCheck(userEmail)
                .flatMapMany(user -> userCameraRepository.findByUserId(user.getId()))
                .flatMap(userCamera -> cameraService.findById(userCamera.getCameraId()));
    }

    @Override
    public Flux<UserDTO> findUsersByCameraEmail(String cameraEmail) {
        return cameraService.findByEmail(cameraEmail)
                .flatMapMany(camera -> userCameraRepository.findByCameraId(camera.getCameraId()))
                .flatMap(userCamera -> userRepository.findById(userCamera.getUserId()))
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<Void> unlinkCameraFromUser(String userEmail, Long cameraId) {
        return userExistsCheck(userEmail)
                .flatMap(user -> userCameraRepository.deleteByUserIdAndCameraId(user.getId(), cameraId));
    }

}


