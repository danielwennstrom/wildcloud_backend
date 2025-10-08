package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

//              !!!!!!!!!!!!!OBS!!!!!!!!!!!
//KOM IHÅG ATT LÄGGA TILL NYA ENDPOINTS TILL SECURITYCONFIGURATION
//SÅ ATT DE INTE KRÄVER AUTHENTICERING FÖR ATT TESTA!!!!!!!!
//              !!!!!!!!!!!!!OBS!!!!!!!!!!!

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {


    @Autowired
    private UserService userService;

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

    @PostMapping("/login")
    public Mono<ResponseEntity<UserDTO>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userService.loginUser(userLoginDTO)
                .doOnNext(user -> System.out.println("User logged in: " + userLoginDTO))
                .map(ResponseEntity::ok);
    }


    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logoutUser(@RequestBody Map<String, String> request) {
        String userEmail = request.get("userEmail");
        return userService.logoutUser(userEmail)
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


    }


    // todo: fix error 401 on update user endpoint(tamas)
    // todo: fix, error 500 log out endpoint(tamas)
    //todo: create new authcontroller(boti)

    // Camera Endpoints

}
