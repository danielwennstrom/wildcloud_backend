package org.wildcloud.wildcloud_backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.entity.RelationEntity.UserCamera;
import org.wildcloud.wildcloud_backend.repository.RelationRepository.UserCameraRepository;
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

    @PostMapping("/users/{userEmail}/cameras/{cameraEmail}")
    public Mono<ResponseEntity<String>> addCameraToUser(
            @PathVariable String userEmail,
            @PathVariable String cameraEmail)
    {

        return userService.assignCameraToUser(cameraEmail, userEmail)
                .then(Mono.just(ResponseEntity.ok("Camera added to user successfully.")))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to add camera to user."));
    }

    @GetMapping("/camerasByUserId/{userId}")
    public Flux<CameraDTO> getCamerasByUserId
            (@PathVariable Long userId
            )
    {
        return userService.getCamerasByUserId(userId);



    }





}
