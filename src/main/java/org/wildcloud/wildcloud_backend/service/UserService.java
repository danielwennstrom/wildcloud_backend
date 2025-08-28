package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UserDTO;
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

    UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO);
    UserDTO addCameraToUser(Long userId, String cameraEmail);

    Set<CameraInfo> getCamerasByUserId(Long id);

    void deleteUser(Long id);




}
