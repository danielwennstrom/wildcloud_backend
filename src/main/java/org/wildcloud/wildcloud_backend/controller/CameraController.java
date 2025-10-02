package org.wildcloud.wildcloud_backend.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.CameraRequestDTO;
import org.wildcloud.wildcloud_backend.repository.CameraRepository;
import org.wildcloud.wildcloud_backend.service.CameraService;
import reactor.core.publisher.Mono;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cameras")
@CrossOrigin(origins = "http://localhost:8080")
public class CameraController {


    @Autowired
    private CameraService cameraService;
    @Autowired
    private CameraRepository cameraRepository;

    @PostMapping("/createCamera")
    public Mono<ResponseEntity<CameraDTO>> registerCamera(@Valid @RequestBody CameraRequestDTO cameraRequestDTO) {
        try {

            return cameraService.registerCamera(cameraRequestDTO)
                    .map(createdCamera -> ResponseEntity.status(HttpStatus.CREATED).body(createdCamera))
                    .doOnSuccess(success -> log.info("Camera registered: {}", cameraRequestDTO.getCameraEmail()))
                    .onErrorResume(error -> {
                        log.error("Error registering camera: {}", error.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                    });
        } catch (Exception e) {
            log.error("Unexpected error registering camera: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }

    }

    @GetMapping("/getAllCameras")
    public Mono<ResponseEntity<List<CameraDTO>>> findAllCameras() {
        return cameraService.findAllCameras()
                .collectList()
                .map(foundCameras -> ResponseEntity.status(HttpStatus.OK).body(foundCameras))
                .doOnSuccess(success -> {
                    if (success.getBody() != null) {
                        log.info("Cameras retrieved: {} cameras found", success.getBody().size());
                    } else {
                        log.info("No cameras found");
                    }
                    log.info("All cameras retrieved: {} cameras found", success.getBody().size());
                })
                .onErrorResume(error -> {
                    log.error("Error retrieving cameras: {}", error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }


    @GetMapping("/getCameraByCameraEmail/{cameraEmail}")
    public Mono<CameraDTO> findCameraByCameraEmail(@PathVariable String cameraEmail) {
        return cameraService.findByEmail(cameraEmail)
                .doOnSuccess(response -> System.out.println("Camera found: " + cameraEmail))
                .doOnError(error -> System.err.println("Error finding camera: " + error.getMessage()));
    }

    @DeleteMapping("/deleteCameraByCameraEmail/{cameraEmail}")
    public Mono<ResponseEntity<Boolean>> deleteCameraByCameraEmail(@PathVariable @Email String cameraEmail) {

        return cameraService.deleteByEmail(cameraEmail)
                .then(Mono.just(ResponseEntity.ok(true)));
    }

    @PutMapping("/updateCamera/{cameraEmail}")
    public Mono<ResponseEntity<CameraDTO>> updateCamera(@Valid @RequestBody CameraRequestDTO cameraRequestDTO, @PathVariable String cameraEmail) {
        return cameraService.updateCamera(cameraRequestDTO, cameraEmail)
                .doOnSuccess(updatedCamera -> log.info("Camera updated: {}", updatedCamera.getCameraEmail()))
                .doOnError(error -> log.error("Error updating camera: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }


}

// todo: add method to link camera to user/s (tamas)
