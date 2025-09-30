package org.wildcloud.wildcloud_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.repository.FileMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageRepository;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {
    private final Map<String, ImageProcessor> processors = new ConcurrentHashMap<>();
    private final List<ImageValidator> validators;
    private final ImageRepository imageRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final ImageMetadataRepository imageMetadataRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    // TODO: implementera events, kan användas till notifications etc.
//    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<UploadResult> processUpload(String sourceType, Object inputData) {
        log.info("processUpload started for sourceType: {}", sourceType);

        return Mono.fromCallable(() -> {
                    ImageProcessor processor = processors.get(sourceType);
                    if (processor == null) {
                        throw new ProcessException("No processor registered for source: " + sourceType);
                    }
                    return processor.process(inputData);
                })
                .flatMap(imageDataList -> {
                    return Flux.fromIterable(imageDataList)
                            .flatMap(this::uploadSingleImage, 4)
                            .collectList();
                })
                .map(uploadedEntities -> {
                    return UploadResult.builder()
                            .metadataList(uploadedEntities)
                            .build();
                })
                .onErrorMap(RuntimeException.class, e ->
                        new UploadException("Upload failed", e)
                );
    }

    public Mono<Image> uploadSingleImage(ImageUploadData data) {
        return Mono.fromCallable(() -> {
                    for (ImageValidator v : validators) {
                        v.validate(data);
                    }
                    return data;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(validatedData -> {
                    String imageKey = buildImageKey(data);

                    return storageService.uploadImage(imageKey,
                                    validatedData.getBuffer(),
                                    validatedData.getFileMetadata().getContentType())
                            .then(Mono.fromCallable(() -> buildImageEntity(validatedData, imageKey)));
                })
                .flatMap(this::saveImageWithMetadata)
                .onErrorMap(RuntimeException.class, e ->
                        new UploadException("Failed to upload " + data.getFileMetadata().getFileName(), e));
    }

    private Mono<Image> saveImageWithMetadata(ImageEntityCreateData createData) {
        return imageRepository.save(createData.getImage())
                .flatMap(savedEntity -> {
                    createData.getFileMetadata().setImageEntityId(savedEntity.getId());
                    createData.getImageMetadata().setImageEntityId(savedEntity.getId());

                    Mono<FileMetadata> savedFileMetadata = fileMetadataRepository.save(createData.getFileMetadata());
                    Mono<ImageMetadata> savedImageMetadata = imageMetadataRepository.save(createData.getImageMetadata());

                    return Mono.zip(savedFileMetadata, savedImageMetadata)
                            .map(tuple -> savedEntity);
                });
    }

    @Override
    public void registerProcessor(String sourceType, ImageProcessor processor) {
        processors.put(sourceType, processor);
        log.info("Registered processor for source type: {}", sourceType);
    }

    @Override
    public void registerValidator(String beanName, ImageValidator validator) {
        validators.add(validator);
        log.info("Registered validator: {}", beanName);
    }

    private String buildImageKey(ImageUploadData imageData) {
        return String.format("images/%s/%s/%s",
                Objects.toString(imageData.getUserId(), "null"),
                Objects.toString(imageData.getCameraId(), "null"),
                imageData.getFileMetadata().getFileName()
        );
    }

    private ImageEntityCreateData buildImageEntity(ImageUploadData data, String imageKey) {
        String sourceMetadataJson = null;
        if (data.getSourceMetadata() != null && !data.getSourceMetadata().isEmpty()) {
            try {
                sourceMetadataJson = objectMapper.writeValueAsString(data.getSourceMetadata());
            } catch (Exception e) {
                log.warn("Failed to serialize source metadata: {}", e.getMessage());
                sourceMetadataJson = "{}";
            }
        }

        Image entity = Image.builder()
                .userId(data.getUserId())
                .cameraId(data.getCameraId())
                .sourceType(data.getSourceType())
                .sourceMetadata(sourceMetadataJson)
                .storageKey(imageKey)
                .build();

        FileMetadata fileMetadata = FileMetadata.builder()
                .fileName(data.getFileMetadata().getFileName())
                .originalFileName(data.getFileMetadata().getOriginalFileName())
                .size(data.getFileMetadata().getSize())
                .contentType(data.getFileMetadata().getContentType())
                .build();

        ImageMetadata imageMetadata = ImageMetadata.builder()
                .capturedAt(data.getImageMetadata().getCapturedAt())
                .lastModified(data.getImageMetadata().getLastModified())
                .build();

        return new ImageEntityCreateData(entity, fileMetadata, imageMetadata);
    }

    @Data
    @AllArgsConstructor
    private static class ImageEntityCreateData {
        private Image image;
        private FileMetadata fileMetadata;
        private ImageMetadata imageMetadata;
    }
}