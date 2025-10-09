package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.*;
import org.wildcloud.wildcloud_backend.security.JwtUtil;
import org.wildcloud.wildcloud_backend.service.RefreshTokenService;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // User Endpoints

    @PostMapping("/createUser")
    public Mono<ResponseEntity<UserDTO>> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("Controller received createUser request for: {}", userRequestDTO.getUserEmail());

        return userService.registerUser(userRequestDTO)
                .doOnSubscribe(s -> log.info("Starting user registration process"))
                .map(userDTO -> {
                    log.info("User creation successful: {}", userDTO.getUserEmail());
                    return ResponseEntity.ok(userDTO);

                })
                .doOnError(error -> log.error("Controller error: {}", error.getMessage(), error))
                .onErrorResume(error -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));
                });
    }

    @PostMapping("/refreshToken")
    public Mono<ResponseEntity<TokenRefreshResponseDTO>> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .flatMap(refreshTokenService::verifyExpiration)
                .flatMap(refreshToken -> userService.findById(refreshToken.getUserId())
                        .map(user -> {
                            String token = jwtUtil.generateToken(user.getEmail());
                            return ResponseEntity.ok(TokenRefreshResponseDTO.builder()
                                    .accessToken(token)
                                    .refreshToken(refreshToken.getToken())
                                    .tokenType("Bearer")
                                    .build());
                        }))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logoutUser(@RequestBody Map<String, String> request) {
        String userEmail = request.get("userEmail");
        return userService.findByUserEmail(userEmail)
                .flatMap(user -> refreshTokenService.deleteByUserId(user.getId())
                        .then(userService.logoutUser(userEmail)))
                .thenReturn(ResponseEntity.ok("User logged out successfully"))
                .onErrorResume(e -> {
                    System.err.println("Error logging out user: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Error logging out user"));
                });
    }

    @GetMapping("/getAllUsers")
    public Mono<ResponseEntity<List<UserDTO>>> getAllUsers() {
        return userService.findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PutMapping("/updateUser/{userEmail}")
    public Mono<ResponseEntity<UserDTO>> updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable String userEmail) {
        return userService.updateUser(userRequestDTO, userEmail)
                .doOnSuccess(updatedUser -> log.info("User updated successfully: {}", updatedUser.getUserEmail()))
                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }


    @DeleteMapping("/deleteUser/{userEmail}")
    public Mono<ResponseEntity<Boolean>> deleteUser(@PathVariable String userEmail) {
        return userService.deleteUser(userEmail)
                .thenReturn(ResponseEntity.ok(true))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false));


    @GetMapping("/getUserById/{userId}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable("userId") Long userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    // todo: log out endpoint(tamas)
    //todo: create new authcontroller(boti)
    //todo: create camera thingies(tamas)

    // Camera Endpoints

}
