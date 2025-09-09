package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO findById(Long userId);
    UserDTO findByEmail(String email);
    UserDTO findByPhoneNumber(Long phoneNumber);
    UserDTO createUser(UserRequestDTO userCompleteDTO);
    UserDTO loginUser(UserLoginDTO userLoginRequest);
    UserDTO updateUser(UserRequestDTO userCompleteDTO, Long userId);
    UserDTO addCameraToUser(Long userId, String cameraEmail);
    Set<CameraInfo> getCamerasByUserId(Long userId);
    void deleteCameraFromUser(Long userId, String cameraEmail);
    void deleteUser(Long id);
}