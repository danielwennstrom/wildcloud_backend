package org.wildcloud.wildcloud_backend.service.Implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.entity.RelationEntity.UserCamera;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.UserNotFoundException;
import org.wildcloud.wildcloud_backend.exception.validator.CameraValidation;
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
    private final CameraValidation cameraValidation;

    public UserServiceIMPL(UserRepository userRepository, UserValidation userValidation,
                           PasswordEncoder passwordEncoder, CameraService cameraService,
                           UserCameraRepository userCameraRepository, CameraValidation cameraValidation) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
        this.passwordEncoder = passwordEncoder;
        this.cameraService = cameraService;
        this.userCameraRepository = userCameraRepository;
        this.cameraValidation = cameraValidation;
    }

    @Override
    public Flux<UserDTO> findAll() {

        return userRepository.findAll()
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findById(Long userId) {
        return userValidation.userExistsByIdValidation(userId)
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findByUserEmail(String email) {
        return userValidation.userExistsByEmailValidation(email)
                .map(this::buildUserDTO);
    }

    @Override
    public Flux<UserDTO> findUsersByCameraId(Long cameraId) {
        return userCameraRepository.findByCameraId(cameraId)
                .flatMap(userCamera -> userRepository.findById(userCamera.getUserId()))
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> findByPhoneNumber(Long phoneNumber) {
        return userValidation.userExistsByPhoneNumberValidation(phoneNumber)
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
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO) {
        log.info("Service method loginUser called with email: {}", userLoginDTO.getUserEmail());

        return userValidation.userExistsByEmailValidation(userLoginDTO.getUserEmail())
                .map(this::buildUserDTO);
    }

    @Override
    public Mono<Void> logoutUser(String userEmail) {
        return userValidation.userExistsByEmailValidation(userEmail)
                .then();
    }


    @Override
    public Mono<UserDTO> updateUser(UserRequestDTO userRequestDTO, String userEmail) {


        return userValidation.userExistsByEmailValidation(userEmail)
                .flatMap(userInfo -> {
                    if (!userEmail.equals(userRequestDTO.getUserEmail())) {
                        return userRepository.findByUserEmail(userRequestDTO.getUserEmail())
                                .flatMap(existsEmail -> Mono.error(
                                        new IllegalArgumentException("Email " + userRequestDTO.getUserEmail() + " is already in use")
                                ))
                                .then(Mono.just(userInfo));
                    }
                    return (Mono.just(userInfo));
                })
                .flatMap(userInfo -> {
                    userInfo.setUserEmail(userEmail);
                    userInfo.setFirstName(userRequestDTO.getFirstName());
                    userInfo.setLastName(userRequestDTO.getLastName());
                    userInfo.setPhoneNumber(userRequestDTO.getPhoneNumber());

                    return userRepository.save(userInfo)
                            .map(this::buildUserDTO);
                });
    }

    @Override
    public Mono<Void> deleteUser(String userEmail) {
        return userValidation.userExistsByEmailValidation(userEmail)
                .flatMap(userInfo -> userRepository.deleteById(userInfo.getId()));
    }



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

    //------------------------ Camera related methods ------------------------
    //------------------------ Camera related methods ------------------------
    //------------------------ Camera related methods ------------------------

    @Override
    public Mono<Void> assignCameraToUser(String cameraEmail, String userEmail) {
        return userValidation.userExistsByEmailValidation(userEmail)
                .zipWith(cameraValidation.cameraExistsByCameraEmailValidation(cameraEmail))
                .flatMap(tuple -> {
                    UserInfo userInfo = tuple.getT1();
                    CameraInfo cameraInfo = tuple.getT2();
                    return userCameraRepository.save(
                            UserCamera.builder()
                                    .userId(userInfo.getId())
                                    .cameraId(cameraInfo.getId())
                                    .build()
                    );
                })
                .then();
    }


    @Override
    public Flux<CameraDTO> getCamerasByUserId(Long userId) {
        return userExistsCheck(userId)
                .flatMapMany(user -> userCameraRepository.findByUserId(user.getId()))
                .flatMap(userCamera -> cameraService.findById(userCamera.getCameraId()))
                .map(this::buildCameraDTO);
    }

    @Override
    public Mono<Void> unlinkCameraFromUser(String userEmail, Long cameraId) {
        return userExistsCheck(userEmail)
                .flatMap(user -> userCameraRepository.deleteByUserIdAndCameraId(user.getId(), cameraId));
    }

    private CameraDTO buildCameraDTO(CameraInfo cameraInfo) {
        return CameraDTO.builder()
                .cameraEmail(cameraInfo.getCameraEmail())
                .cameraName(cameraInfo.getCameraName())
                .cameraId(cameraInfo.getId())
                .build();
    }


}


