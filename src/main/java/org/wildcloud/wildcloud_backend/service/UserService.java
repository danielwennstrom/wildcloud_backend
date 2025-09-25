package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface UserService {

    Flux<UserDTO> findAll();
    Mono<UserDTO> findById(Long userId);
    Mono<UserDTO> findByUserEmail(String email);
    Mono<UserDTO> findByPhoneNumber(Long phoneNumber);
    Mono<UserDTO> registerUser(UserRequestDTO userCompleteDTO);
    Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO);
    Mono<UserDTO> updateUser(UserRequestDTO userCompleteDTO, Long userId);
    Mono<Void> deleteUser(Long userId);

}