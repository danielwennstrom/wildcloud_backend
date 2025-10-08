package org.wildcloud.wildcloud_backend.exception.validator;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.repository.CameraRepository;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CameraValidation {

    private final CameraRepository cameraRepository;

    public CameraValidation(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }


    public Mono<CameraInfo> cameraExistsByCameraEmailValidation(String cameraEmail) {
        log.info("Camera exists validation {}", cameraEmail);
        return cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new RuntimeException("Camera with email " + cameraEmail + " not found")))
                .doOnSuccess(cameraInfo -> log.info("Camera found: {}", cameraInfo.getCameraEmail()));
    }

}
