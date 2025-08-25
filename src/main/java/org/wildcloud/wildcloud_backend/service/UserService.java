package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserRegistrationDTO;
import org.wildcloud.wildcloud_backend.dto.UserUpdateDTO;
import org.wildcloud.wildcloud_backend.entity.UserInfo;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO findById(Long id);
    UserDTO findByEmail(String email);
    UserDTO findByPhoneNumber(Long phoneNumber);
    UserDTO createUser(String email, String firstName, String lastName, Long phoneNumber, String password, String cameraEmail);

    UserDTO updateUser(String email, String firstName, String lastName, Long phoneNumber, String password, String cameraEmail);
    UserDTO addCameraToUser(Long userId, String cameraEmail);
    void deleteUser(Long id);




}
