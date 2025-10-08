package org.wildcloud.wildcloud_backend.service;


import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.CameraRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CameraService {

    Flux<CameraDTO> findAllCameras();

    Mono<CameraDTO> findByEmail(String cameraEmail);

    Mono<CameraInfo> findById(Long id);

    Mono<CameraDTO> registerCamera(CameraRequestDTO cameraRequestDTO);

    Mono<CameraDTO> updateCamera(CameraRequestDTO cameraRequestDTO, String cameraEmail);

    Mono<Void> deleteByEmail(String cameraEmail);



}
