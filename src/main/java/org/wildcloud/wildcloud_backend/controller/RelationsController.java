package org.wildcloud.wildcloud_backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.UserCameraRequestDTO;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/relationships")
public class RelationsController {

    private final UserService userService;

    public RelationsController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/assignCameraToUser")
    public Mono<ResponseEntity<String>> addCameraToUser(@RequestBody UserCameraRequestDTO request)
    {
        System.out.println("Received request to add camera to user: " + request.getUserEmail() + " and camera: " + request.getCameraEmail());
        return userService.assignCameraToUser(request.getCameraEmail(), request.getUserEmail())
                .then(Mono.just(ResponseEntity.ok("Camera added to user successfully.")))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to add camera to user."));
    }

    @GetMapping("/camerasByUserId/{userId}")
    public Flux<CameraDTO> getCamerasByUserId(@PathVariable Long userId) {
        System.out.println(userService.getCamerasByUserId(userId).toString());
        return userService.getCamerasByUserId(userId);
    }

}
