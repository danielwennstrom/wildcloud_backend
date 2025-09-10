package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.UserDTO;
import org.wildcloud.wildcloud_backend.dto.UserLoginDTO;
import org.wildcloud.wildcloud_backend.dto.UserRequestDTO;
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

    // User Endpoints

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserDTO userCreated = userService.registerUser(userRequestDTO);
        return ResponseEntity.ok(userCreated);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserDTO userLoginResponse = userService.loginUser(userLoginDTO);
        return ResponseEntity.ok(userLoginResponse);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserRequestDTO userRequestDTO, @PathVariable Long userId) {
        UserDTO updatedCreated = userService.updateUser(userRequestDTO,userId);
        return ResponseEntity.ok(updatedCreated);
    }


    // todo: log out endpoint

    // Camera Endpoints

    @PostMapping("/{userId}/cameras/{cameraEmail}")
    public ResponseEntity<UserDTO> addCameraToUser(@PathVariable Long userId,
                                                  @RequestBody CameraInfo cameraInfo) {
        //cameraService.addCamera(cameraInfo);
        return ResponseEntity.ok(userService.addCameraToUser(userId, cameraInfo.getCameraEmail()));
    } // todo: uncomment line when camera service is implemented

    @GetMapping("/{userId}/cameras")
    public ResponseEntity<Set<CameraInfo>> getCamerasByUserId(@PathVariable Long userId) {
        Set<CameraInfo> cameras = userService.getCamerasByUserId(userId);
        return ResponseEntity.ok(cameras);
    }


}
