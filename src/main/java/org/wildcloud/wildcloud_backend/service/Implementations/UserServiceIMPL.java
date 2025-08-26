package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserRegistrationDTO;
import org.wildcloud.wildcloud_backend.dto.UserUpdateDTO;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceIMPL implements UserService {


    private final UserRepository userRepository;

    public UserServiceIMPL(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserInfo> userInfoList = userRepository.findAll();


        return userInfoList.stream().map(userInfo -> UserDTO.builder()
                        .id(userInfo.getId())
                        .email(userInfo.getEmail())
                        .phoneNumber(userInfo.getPhoneNumber())
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .cameraEmail(userInfo.getCameraEmail())
                        .build())
                .collect(toList());
    }

    @Override
    public UserDTO findById(Long id) {
        UserInfo userInfo = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        return UserDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserDTO findByEmail(String email) {
        UserInfo userInfo = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        return UserDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserDTO findByPhoneNumber(Long phoneNumber) {
        UserInfo userInfo = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("Phone number not found"));

        return UserDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserRegistrationDTO createUser(String email, String firstName, String lastName, Long phoneNumber, String password, String cameraEmail) {
        try {
            if (userRepository.existsByEmail(email)) {
                UserInfo newUser = userRepository.save(UserInfo.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phoneNumber(phoneNumber)
                        .password(password)
                        .cameraEmail(cameraEmail)
                        .build());
                return UserUpdateDTO.builder()
                        .id(newUser.getId())
                        .email(newUser.getEmail())
                        .phoneNumber(newUser.getPhoneNumber())
                        .firstName(newUser.getFirstName())
                        .lastName(newUser.getLastName())
                        .build();
            }
    } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Email already exists");
    }

    @Override
    @Transactional
    public UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO) {

        UserInfo userInfo = userRepository.findByEmail(userUpdateDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User with email " + userUpdateDTO.getEmail() + " not found"));

        // Update fields if they are provided and different
        if (userUpdateDTO.getFirstName() != null && !userUpdateDTO.getFirstName().isBlank() &&
                !userUpdateDTO.getFirstName().equals(userInfo.getFirstName())) {
            userInfo.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null && !userUpdateDTO.getLastName().isBlank() &&
                !userUpdateDTO.getLastName().equals(userInfo.getLastName())) {
            userInfo.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPhoneNumber() != null &&
                !userUpdateDTO.getPhoneNumber().equals(userInfo.getPhoneNumber())) {
            userInfo.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
//TODO: Skapa en metod för att lägga till kamera och ta bort kamera från en user.


        // Save the updated entity
        userRepository.save(userInfo);

        // Return the updated user as a DTO
        return (UserUpdateDTO) UserUpdateDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .phoneNumber(userInfo.getPhoneNumber())
                .build();
    }

    @Override
    @Transactional
    public UserDTO addCameraToUser(Long userId, String cameraEmail) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserInfo userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!cameraEmail.equals(userInfo.getCameraEmail())) {
            userInfo.setCameraEmail(cameraEmail);
        }
        return persist(userInfo);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with id " + id + " does not exist");
        }
        userRepository.deleteById(id);

    }


    private UserDTO persist(UserInfo userInfo) {
        return UserDTO.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }
}
