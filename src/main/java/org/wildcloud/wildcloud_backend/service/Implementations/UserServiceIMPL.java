package org.wildcloud.wildcloud_backend.service.Implementations;

import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserRegistrationDTO;
import org.wildcloud.wildcloud_backend.dto.UserUpdateDTO;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.List;

@Service
public class UserServiceIMPL implements UserService {




    @Override
    public List<UserDTO> findAll() {
        return List.of();
    }

    @Override
    public UserDTO findById(Long id) {
        return null;
    }

    @Override
    public UserDTO findByEmail(String email) {
        return null;
    }

    @Override
    public UserDTO findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public UserDTO createUser(UserRegistrationDTO userRegistrationDTO) {
        return null;
    }

    @Override
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO) {
        return null;
    }

    @Override
    public UserDTO addCameraToUser(Long userId, String cameraEmail) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
