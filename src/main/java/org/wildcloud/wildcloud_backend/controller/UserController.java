package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRegistrationDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.List;
import java.util.Set;

//              !!!!!!!!!!!!!OBS!!!!!!!!!!!
//KOM IHÅG ATT LÄGGA TILL NYA ENDPOINTS TILL SECURITYCONFIGURATION
//SÅ ATT DE INTE KRÄVER AUTHENTICERING FÖR ATT TESTA!!!!!!!!
//              !!!!!!!!!!!!!OBS!!!!!!!!!!!

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping("/createUser")
    public ResponseEntity<UserRegistrationDTO> createUser(@Valid @RequestBody UserRegistrationDTO userRegistrationRequest) {
        UserRegistrationDTO userCreated = userService.createUser(userRegistrationRequest);
        return ResponseEntity.ok(userCreated);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginRequest) {
        UserLoginDTO userLoginResponse = userService.loginUser(userLoginRequest);
        return ResponseEntity.ok(userLoginResponse);
    }


    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/cameras/{cameraEmail}")
    public ResponseEntity<String> addCameraToUser(@PathVariable Long userId,@PathVariable String cameraEmail ) {
        userService.addCameraToUser(userId, cameraEmail);                            // todo: change cameraEmail PathVariable to RequestBody
        return ResponseEntity.ok("Camera added to user successfully");          // and then call on a CameraService to add camera and then
    }                                                                                  // userService to add camera to user

    @GetMapping("/{userId}/cameras")
    public ResponseEntity<Set<String>> getCamerasByUserId(@PathVariable Long userId) {
        Set<String> cameras = userService.getCamerasByUserId(userId);
        return ResponseEntity.ok(cameras);
    }
}
