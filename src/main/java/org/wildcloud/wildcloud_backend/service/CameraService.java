package org.wildcloud.wildcloud_backend.service;


import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.CameraRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CameraService {

    Flux<CameraDTO> findAllCameras();
    Mono<CameraDTO> findCameraByEmail(String cameraEmail);
    Mono<CameraDTO> findCameraById(Long cameraId);
    Mono<CameraDTO> registerCamera(CameraRequestDTO cameraRequestDTO);
    Mono<CameraDTO> updateCamera(CameraRequestDTO cameraRequestDTO, Long cId);
    Mono<Void> deleteCamera(String cameraEmail);

}
