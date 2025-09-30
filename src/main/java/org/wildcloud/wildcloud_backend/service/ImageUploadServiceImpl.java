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
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.model.UploadSummary;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.registrar.ProcessorRegistrar;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {
    private final ProcessorRegistrar processorRegistrar;
    private final List<ImageValidator> validators;
    private final ImageRepository imageRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final ImageMetadataRepository imageMetadataRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    // TODO: implementera events, kan användas till notifications etc.
//    private final ApplicationEventPublisher eventPublisher;

    public Mono<UploadSummary> processUpload(SourceType sourceType, Object inputData) {
        log.info("processUpload started for sourceType: {}", sourceType);
        ImageProcessor processor = processorRegistrar.getProcessor(sourceType);

        if (processor == null) {
            throw new ProcessException("No processor registered for source: " + sourceType);
        }

        return Mono.fromCallable(() -> processor.process(inputData))
                .flatMapMany(imageDataList ->
                        Flux.fromIterable(imageDataList)
                                .flatMap(data ->
                                                uploadSingleImage(data)
                                                        .map(img -> UploadResult.success(data.getFileMetadata().getFileName()))
                                                        .onErrorResume(e -> Mono.just(
                                                                UploadResult.failure(
                                                                        data.getFileMetadata().getOriginalFileName(),
                                                                        e.getMessage()
                                                                )
                                                        )),
                                        5 // concurrency
                                )
                )
                .collectList()
                .map(results -> {
                    Map<Boolean, List<UploadResult>> partitioned = results.stream()
                            .collect(Collectors.partitioningBy(UploadResult::isSuccess));

                    return UploadSummary.builder()
                            .successes(partitioned.get(true))
                            .failures(partitioned.get(false))
                            .build();
                })
                .onErrorMap(e -> new UploadException("Upload failed", e));
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
                            .then(Mono.fromCallable(() -> buildImageCreateData(validatedData, imageKey)));
                })
                .flatMap(this::saveImageWithMetadata)
                .onErrorMap(RuntimeException.class, e ->
                        new UploadException("Failed to upload " + data.getFileMetadata().getFileName(), e));
    }

    private Mono<Image> saveImageWithMetadata(ImageCreateData createData) {
        return imageRepository.save(createData.getImage())
                .flatMap(savedEntity -> {
                    createData.getFileMetadata().setImageEntityId(savedEntity.getId());
                    createData.getImageMetadata().setImageEntityId(savedEntity.getId());

                    Mono<FileMetadata> savedFileMetadata = fileMetadataRepository.save(createData.getFileMetadata());
                    Mono<ImageMetadata> savedImageMetadata = imageMetadataRepository.save(createData.getImageMetadata());

                    return Mono.zip(savedFileMetadata, savedImageMetadata)
                            .map(tuple -> savedEntity);
                })
                .onErrorMap(e -> new RuntimeException("Failed to save image to database: " + createData.fileMetadata.getOriginalFileName()));
    }

    private String buildImageKey(ImageUploadData imageData) {
        return String.format("images/%s/%s/%s",
                Objects.toString(imageData.getUserId(), "null"),
                Objects.toString(imageData.getCameraId(), "null"),
                imageData.getFileMetadata().getFileName()
        );
    }

    private ImageCreateData buildImageCreateData(ImageUploadData data, String imageKey) {
        String sourceMetadataJson = null;
        if (data.getSourceMetadata() != null && !data.getSourceMetadata().isEmpty()) {
            try {
                sourceMetadataJson = objectMapper.writeValueAsString(data.getSourceMetadata());
            } catch (Exception e) {
                log.warn("Failed to serialize source metadata: {}", e.getMessage());
                sourceMetadataJson = "{}";
            }
        }

        Image image = Image.builder()
                .userId(data.getUserId())
                .cameraId(data.getCameraId())
                .sourceType(data.getSourceType())
                .sourceMetadata(sourceMetadataJson)
                .storageKey(imageKey)
                .build();

        FileMetadata fileMetadata = data.getFileMetadata();
        ImageMetadata imageMetadata = data.getImageMetadata();

        return new ImageCreateData(image, fileMetadata, imageMetadata);
    }

    @Data
    @AllArgsConstructor
    private static class ImageCreateData {
        private Image image;
        private FileMetadata fileMetadata;
        private ImageMetadata imageMetadata;
    }
}