package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.*;
import org.wildcloud.wildcloud_backend.security.JwtUtil;
import org.wildcloud.wildcloud_backend.service.RefreshTokenService;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // User Endpoints

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

    @DeleteMapping("/deleteUser/{userId}")
    public Mono<ResponseEntity<Boolean>> deleteUser(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId)
                .thenReturn(ResponseEntity.ok(true))
                .onErrorReturn(ResponseEntity.ok(false));
    }

    @PostMapping("/updateUser/{userId}")
    public Mono<ResponseEntity<UserDTO>> updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable("userId") Long userId) {

    return userService.updateUser(userRequestDTO, userId)
            .map(ResponseEntity::ok);
    }



    // todo: log out endpoint(tamas)
    //todo: create new authcontroller(boti)
    //todo: create camera thingies(tamas)

    // Camera Endpoints

}
