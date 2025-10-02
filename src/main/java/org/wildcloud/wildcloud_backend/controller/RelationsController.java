package org.wildcloud.wildcloud_backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.repository.UserRepository;
import org.wildcloud.wildcloud_backend.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/relationships")
@CrossOrigin(origins = "http://localhost:8080")
public class RelationsController {

    private final UserService userService;

    public RelationsController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/{userEmail}/cameras/{cameraId}")
    public Mono<ResponseEntity<String>> addCameraToUser(
            @PathVariable String userEmail,
            @PathVariable Long cameraId)
    {

        return userService.assignCameraToUser(cameraId, userEmail)
                .then(Mono.just(ResponseEntity.ok("Camera added to user successfully.")))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to add camera to user."));
    }




}
