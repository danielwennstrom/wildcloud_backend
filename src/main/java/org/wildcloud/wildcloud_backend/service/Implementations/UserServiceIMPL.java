package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserUpdateDTO;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.repository.UserRepo;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceIMPL implements UserService {


    private final UserRepo userRepo;

    public UserServiceIMPL(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserInfo> userInfoList = userRepo.findAll();


        return userInfoList.stream().map(UserInfo -> UserDTO.builder()
                        .userId(UserInfo.getId())
                        .email(UserInfo.getEmail())
                        .phoneNumber(UserInfo.getPhoneNumber())
                        .firstName(UserInfo.getFirstName())
                        .lastName(UserInfo.getLastName())
                        .cameraEmail(UserInfo.getCameraEmail())
                        .build())
                .collect(toList());
    }

    @Override
    public UserDTO findById(Long id) {
        UserInfo userInfo = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));

        return UserDTO.builder()
                .userId(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserDTO findByEmail(String email) {
        UserInfo userInfo = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        return UserDTO.builder()
                .userId(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserDTO findByPhoneNumber(Long phoneNumber) {
        UserInfo userInfo = userRepo.findByPhoneNumber(phoneNumber).orElseThrow(() -> new RuntimeException("Phone number not found"));

        return UserDTO.builder()
                .userId(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }

    @Override
    public UserDTO createUser(String email, String firstName, String lastName, Long phoneNumber, String password, String cameraEmail) {
        try {
            if (!userRepo.findByEmail(email).isPresent()) {
                UserInfo newUser = userRepo.save(UserInfo.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phoneNumber(phoneNumber)
                        .password(password)
                        .cameraEmail(cameraEmail)
                        .build());
                return UserDTO.builder()
                        .userId(newUser.getId())
                        .email(newUser.getEmail())
                        .phoneNumber(newUser.getPhoneNumber())
                        .firstName(newUser.getFirstName())
                        .lastName(newUser.getLastName())
                        .cameraEmail(newUser.getCameraEmail())
                        .build();
            } else if (password.equals(userRepo.findByEmail(email).get().getPassword())) {
                UserInfo userInfo = userRepo.save(UserInfo.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phoneNumber(phoneNumber)
                        .cameraEmail(cameraEmail)
                        .build());

                return UserDTO.builder()
                        .userId(userInfo.getId())
                        .email(userInfo.getEmail())
                        .phoneNumber(userInfo.getPhoneNumber())
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .cameraEmail(userInfo.getCameraEmail())
                        .build();

            }
            throw new RuntimeException("User creation failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public UserDTO updateUser(String email, String firstName, String lastName, Long phoneNumber, String password, String cameraEmail) {
        UserInfo userInfo = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        if (firstName != null && !firstName.isBlank() && !firstName.equals(userInfo.getFirstName())) {
            userInfo.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isBlank() && !lastName.equals(userInfo.getLastName())) {
            userInfo.setLastName(lastName);
        }
        if (phoneNumber != null && !phoneNumber.equals(userInfo.getPhoneNumber())) {
            userInfo.setPhoneNumber(phoneNumber);
        }
        if (password != null && !password.isBlank() && !password.equals(userInfo.getPassword())) {
            userInfo.setPassword(password);
        }
        if (cameraEmail != null && !cameraEmail.isBlank() && !cameraEmail.equals(userInfo.getCameraEmail())) {
            userInfo.setCameraEmail(cameraEmail);
        }

        return persist(userInfo);
    }

    @Override
    @Transactional
    public UserDTO addCameraToUser(Long userId, String cameraEmail) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserInfo userInfo = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!cameraEmail.equals(userInfo.getCameraEmail())) {
            userInfo.setCameraEmail(cameraEmail);
        }
        return persist(userInfo);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User with id " + id + " does not exist");
        }
        userRepo.deleteById(id);

    }


    private UserDTO persist(UserInfo userInfo) {
        return UserDTO.builder()
                .userId(userInfo.getId())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .cameraEmail(userInfo.getCameraEmail())
                .build();
    }
}
