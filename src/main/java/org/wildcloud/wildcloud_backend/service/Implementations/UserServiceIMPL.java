package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.exception.custom.*;
import org.wildcloud.wildcloud_backend.repository.CameraRepository;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceIMPL implements UserService {

    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceIMPL(UserRepository userRepository, CameraRepository cameraRepository) {
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserInfo> userInfoList = userRepository.findAll();

        return userInfoList.stream()
                .map(this::buildUserDTO)
                .collect(toList());
    }

    @Override
    public UserDTO findById(Long userId) {
        UserInfo userInfo = userExistsCheck(userId);
        return buildUserDTO(userInfo);
    }

    @Override
    public UserDTO findByEmail(String email) {
        UserInfo userInfo = userExistsCheck(email);
        return buildUserDTO(userInfo);
    }

    @Override
    public UserDTO findByPhoneNumber(Long phoneNumber) {
        UserInfo userInfo = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PhoneNumberNotFoundException("Phone number not found"));

        return buildUserDTO(userInfo);
    }

    @Override
    public UserDTO registerUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new EmailTakenException("User with email " + userRequestDTO.getEmail() + " already exists");
        }

        userRequestDTO.setPassword(bCryptPasswordEncoder.encode(userRequestDTO.getPassword()));

        UserInfo newUser = userRepository.save(UserInfo.builder()
                        .email(userRequestDTO.getEmail())
                        .firstName(userRequestDTO.getFirstName())
                        .lastName(userRequestDTO.getLastName())
                        .phoneNumber(userRequestDTO.getPhoneNumber())
                        .password(userRequestDTO.getPassword())
                        .build());

        return buildUserDTO(newUser);
    }

    @Override
    public UserDTO loginUser(UserLoginDTO userLoginDTO) {
        UserInfo userLoginInfo = userExistsCheck(userLoginDTO.getEmail());
        if (!bCryptPasswordEncoder.matches(userLoginDTO.getPassword(), userLoginInfo.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        return buildUserDTO(userLoginInfo);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserRequestDTO userRequestDTO, Long userId) {

        UserInfo userInfo = userExistsCheck(userId);

        if (!userRequestDTO.getEmail().equals(userInfo.getEmail())) {
            if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
                throw new EmailTakenException("User with email " + userRequestDTO.getEmail() + " already exists");
            }
            userInfo.setEmail(userRequestDTO.getEmail());
        }

        if (!userRequestDTO.getPassword().equals(userInfo.getPassword())) {
            userInfo.setPassword(userRequestDTO.getPassword());
        }

        if (!userRequestDTO.getFirstName().equals(userInfo.getFirstName())) {
            userInfo.setFirstName(userRequestDTO.getFirstName());
        }

        if (!userRequestDTO.getLastName().equals(userInfo.getLastName())) {
            userInfo.setLastName(userRequestDTO.getLastName());
        }

        if (!userRequestDTO.getPhoneNumber().equals(userInfo.getPhoneNumber())) {
            userInfo.setPhoneNumber(userRequestDTO.getPhoneNumber());
        }

        userRepository.save(userInfo);

        return buildUserDTO(userInfo);
    }

    @Override
    @Transactional
    public UserDTO addCameraToUser(Long userId, String cameraEmail) {

        UserInfo userInfo = userExistsCheck(userId);
        CameraInfo camera = cameraExistsCheck(cameraEmail);

        if (userInfo.getCameras().contains(camera)) {
            throw new UserAlreadyOwnsException("Camera already added to the user");
        }

        userInfo.getCameras().add(camera);
        userRepository.save(userInfo);

        return buildUserDTO(userInfo);
    }



    @Override
    @Transactional
    public Set<CameraInfo> getCamerasByUserId(Long userId) {
        UserInfo userInfo = userExistsCheck(userId);
        return new HashSet<>(userInfo.getCameras());
    }

    @Override
    @Transactional
    public void removeCameraFromUser(Long userId, String cameraEmail) {
        UserInfo userInfo = userExistsCheck(userId);
        CameraInfo camera = cameraExistsCheck(cameraEmail);

        if (!userInfo.getCameras().contains(camera))
            throw new CameraNotAssociatedWithUserException("Camera not associated with user");

        userInfo.getCameras().remove(camera);
        userRepository.save(userInfo);
    }

    @Override
    public void deleteUser(Long userId) {
        userExistsCheck(userId);
        userRepository.deleteById(userId);
    }


    private UserDTO buildUserDTO(UserInfo userInfo) {
        return UserDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .build();
    }

    private UserInfo userExistsCheck(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    private UserInfo userExistsCheck(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    private CameraInfo cameraExistsCheck(String cameraEmail) {
        return cameraRepository.findById(cameraEmail)
                .orElseThrow(() -> new CameraNotFoundException("Camera not found"));
    }
}