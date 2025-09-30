package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.security.JwtUtil;
import org.wildcloud.wildcloud_backend.service.RefreshTokenService;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/createUser")
    public Mono<ResponseEntity<UserDTO>> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.registerUser(userRequestDTO)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Error registering user: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).build());
                })
                .doOnSuccess(response -> System.out.println("User registered: " + userRequestDTO));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userService.loginUser(userLoginDTO)
                .flatMap(userDTO -> refreshTokenService.createRefreshToken(userDTO.getId())
                        .map(refreshToken -> {
                            String accessToken = jwtUtil.generateToken(userDTO.getEmail());
                            return ResponseEntity.ok(Map.of(
                                    "user", userDTO,
                                    "accessToken", accessToken,
                                    "refreshToken", refreshToken.getToken(),
                                    "tokenType", "Bearer"
                            ));
                        }));
    }
}
