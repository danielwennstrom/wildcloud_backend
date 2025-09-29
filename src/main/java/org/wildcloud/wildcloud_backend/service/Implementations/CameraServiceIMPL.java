package org.wildcloud.wildcloud_backend.service.Implementations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.CameraRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.exception.custom.CameraNotFoundException;
import org.wildcloud.wildcloud_backend.exception.custom.EmailTakenException;
import org.wildcloud.wildcloud_backend.repository.CameraRepository;
import org.wildcloud.wildcloud_backend.service.CameraService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CameraServiceIMPL implements CameraService {

    private final CameraRepository cameraRepository;


    public CameraServiceIMPL(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }


    @Override
    public Flux<CameraDTO> findAllCameras() {
        log.info("Finding all cameras");
        return cameraRepository.findAll()
                .map(this::buildCameraDTO);

    }

    @Override
    public Mono<CameraDTO> findCameraByEmail(String cameraEmail) {
        log.info("Finding camera with email: {}", cameraEmail);
        return cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new CameraNotFoundException("Camera with email " + cameraEmail + " not found")))
                .map(this::buildCameraDTO);
    }

    @Override
    public Mono<CameraDTO> findCameraById(Long cameraId) {
        log.info("Finding camera with Id: {}", cameraId);
        return cameraRepository.findById(cameraId)
                .switchIfEmpty(Mono.error(new CameraNotFoundException("Camera with Id " + cameraId + " not found")))
                .map(this::buildCameraDTO);
    }

    @Override
    public Mono<CameraDTO> registerCamera(CameraRequestDTO cameraRequestDTO) {
        try {
            return cameraRepository.existsByCameraEmail(cameraRequestDTO.getCameraEmail())
                    .flatMap(exists -> {
                        if (Boolean.TRUE.equals(exists)) {
                            return Mono.error(new EmailTakenException("There is a already a camera registered with: " + cameraRequestDTO.getCameraEmail()));
                        }
                        CameraInfo newCamera = CameraInfo.builder()
                                .cameraEmail(cameraRequestDTO.getCameraEmail())
                                .cameraName(cameraRequestDTO.getCameraName())
                                .build();
                        return cameraRepository.save(newCamera)
                                .map(this::buildCameraDTO)
                                .doOnError(c -> System.err.println("Error saving camera: " + cameraRequestDTO));
                    });
        } catch (Exception e) {
            log.error("Error saving camera: {}", e, e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<CameraDTO> updateCamera(CameraRequestDTO cameraRequestDTO, String cameraEmail) {

        return cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new CameraNotFoundException("Camera with email: " + cameraEmail + " not found")))
                .flatMap(cameraInfo -> {
                    cameraInfo.setCameraEmail(cameraRequestDTO.getCameraEmail());
                    cameraInfo.setCameraName(cameraRequestDTO.getCameraName());
                    return cameraRepository.save(cameraInfo)
                            .map(this::buildCameraDTO);
                });
    }

    @Override
    public Mono<Void> deleteCamera(String cameraEmail) {
        return cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new CameraNotFoundException("Camera with email " + cameraEmail + " not found")))
                .flatMap(cameraInfo -> {
                    Long cameraId = cameraInfo.getId();
                    log.info("Deleting camera with Id= {}, email= {}", cameraId, cameraEmail);

                    return cameraRepository.delete(cameraInfo)
                            .doOnSuccess(success -> log.info("Camera with Id= {}, email= {} deleted successfully", cameraId, cameraEmail));
                });

    }


    private CameraDTO buildCameraDTO(CameraInfo cameraInfo) {
        return CameraDTO.builder()
                .cameraId(cameraInfo.getId())
                .cameraEmail(cameraInfo.getCameraEmail())
                .cameraName(cameraInfo.getCameraName())
                .build();
    }

}
