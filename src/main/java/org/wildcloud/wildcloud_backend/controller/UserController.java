package org.wildcloud.wildcloud_backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.service.UserService;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/cameras/{cameraEmail}")
    public ResponseEntity<String> addCameraToUser(@PathVariable Long userId,@PathVariable String cameraEmail ) {
        userService.addCameraToUser(userId, cameraEmail);
        return ResponseEntity.ok("Camera added to user successfully");

    }

    @GetMapping("/{userId}/cameras")
    public ResponseEntity<Set<String>> getCamerasByUserId(Long userId) {
        Set<String> cameras = userService.getCamerasByUserId(userId);
        return ResponseEntity.ok(cameras);
    }




}
