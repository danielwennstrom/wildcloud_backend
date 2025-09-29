package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<UserDTO> findAll();

    Mono<UserDTO> findById(Long userId);

    Mono<UserDTO> findByUserEmail(String userEmail);

    Mono<UserDTO> findByPhoneNumber(Long phoneNumber);

    Mono<UserDTO> registerUser(UserRequestDTO userCompleteDTO);

    Mono<UserDTO> loginUser(UserLoginDTO userLoginDTO);

    Mono<UserDTO> updateUser(UserRequestDTO userCompleteDTO, String userEmail);

    Mono<Void> deleteUser(String userEmail);

    Mono<Void> logoutUser(String userEmail);
}