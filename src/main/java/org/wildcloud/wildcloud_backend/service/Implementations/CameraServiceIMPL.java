package org.wildcloud.wildcloud_backend.service.Implementations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.CameraDTO;
import org.wildcloud.wildcloud_backend.dto.CameraRequestDTO;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.exception.custom.EmailTakenException;
import org.wildcloud.wildcloud_backend.repository.CameraRepository;
import org.wildcloud.wildcloud_backend.service.CameraService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CameraServiceIMPL implements CameraService {

    @Autowired
    private CameraRepository cameraRepository;


    public CameraServiceIMPL(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }









    @Override
    public Flux<CameraDTO> findAllCameras() {

        return cameraRepository.findAll()
                .map(this::buildCameraDTO);

    }

    @Override
    public Mono<CameraDTO> findCameraByEmail(String cameraEmail) {
        return  cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new EmailTakenException("Camera with email " + cameraEmail + " not found")))
                .map(this::buildCameraDTO);
    }

    @Override
    public Mono<CameraDTO> findCameraById(Long cId) {
        return null;
    }

    @Override
    public Mono<CameraDTO> registerCamera(CameraRequestDTO cameraRequestDTO) {

        return cameraRepository.existsByCameraEmail(cameraRequestDTO.getCameraEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailTakenException("There is a already a camera registered with: " + cameraRequestDTO.getCameraEmail()));
                    }
                    CameraInfo newCamera = CameraInfo.builder()
                            .cameraEmail(cameraRequestDTO.getCameraEmail())
                            .cameraName(cameraRequestDTO.getCameraName())
                            .build();
                    return  cameraRepository.save(newCamera)
                            .map(this::buildCameraDTO)
                            .doOnError(c -> System.err.println("Error saving camera: " + cameraRequestDTO));
                });
    }

    @Override
    public Mono<CameraDTO> updateCamera(CameraRequestDTO cameraRequestDTO, Long cId) {
        return null;
    }

    @Override
    public Mono<Void> deleteCamera(String cameraEmail) {
        return cameraRepository.findByCameraEmail(cameraEmail)
                .switchIfEmpty(Mono.error(new EmailTakenException("Camera with email " + cameraEmail + " not found")))
                .flatMap(cameraInfo ->{
                    Long cameraId = cameraInfo.getId();
                    log.info("Deleting camera with Id= {}, email= {}", cameraId, cameraEmail );

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
