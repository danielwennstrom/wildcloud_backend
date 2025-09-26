package org.wildcloud.wildcloud_backend.repository;


import lombok.Data;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CameraRepository extends ReactiveCrudRepository<CameraInfo,Long> {

    Mono<CameraInfo> findByCameraEmail(String cameraEmail);
    Mono<Boolean> existsByCameraEmail(String cameraEmail);
    Flux<CameraInfo> findUserByCameraEmail(String cameraEmail);
    Mono<CameraInfo> findById(Long cameraId);


}
