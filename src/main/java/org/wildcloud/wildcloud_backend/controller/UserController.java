package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.service.Implementations.UserServiceIMPL;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

//              !!!!!!!!!!!!!OBS!!!!!!!!!!!
//KOM IHÅG ATT LÄGGA TILL NYA ENDPOINTS TILL SECURITYCONFIGURATION
//SÅ ATT DE INTE KRÄVER AUTHENTICERING FÖR ATT TESTA!!!!!!!!
//              !!!!!!!!!!!!!OBS!!!!!!!!!!!

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {


    @Autowired
    private UserService userService;

    // User Endpoints

    @PostMapping("/createUser")
    public Mono<ResponseEntity<UserDTO>> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.registerUser(userRequestDTO)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserDTO>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return userService.loginUser(userLoginDTO)
                .doOnNext(user -> System.out.println("User logged in: " + userLoginDTO))
                .map(ResponseEntity::ok);
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


    // todo: log out endpoint

    // Camera Endpoints

}
