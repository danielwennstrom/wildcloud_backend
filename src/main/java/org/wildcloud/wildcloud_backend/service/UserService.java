package org.wildcloud.wildcloud_backend.service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.transaction.annotation.Transactional;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRegistrationDTO;
import org.wildcloud.wildcloud_backend.dto.UserUpdateDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO findByEmail(String email);
    UserDTO findByPhoneNumber(Long phoneNumber);
    UserRegistrationDTO createUser(UserRegistrationDTO userRegistrationDTO);
    UserLoginDTO loginUser(UserLoginDTO userLoginRequest);

    UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO);

    @Transactional
    Set<String> getCameraEmailByUserId(Long id);

    UserDTO addCameraToUser(Long userId, String cameraEmail);

    Set<String> getCamerasByUserId(Long id);

    void deleteCameraFromUser(Long userId, String cameraEmail);

    void deleteUser(Long id);


}