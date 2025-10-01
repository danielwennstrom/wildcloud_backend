package org.wildcloud.wildcloud_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
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

    public Mono<UploadSummary> processUpload(SourceType sourceType, ImageUploadContext context) {
        log.info("processUpload started for sourceType: {}", sourceType);
        ImageProcessor processor = processorRegistrar.getProcessor(sourceType);

        if (processor == null) {
            throw new ProcessException("No processor registered for source: " + sourceType);
        }

        return Mono.fromCallable(() -> processor.process(context))
                .flatMapMany(imageDataList ->
                        Flux.fromIterable(imageDataList)
                                .flatMap(fileData -> validate(fileData)
                                        .flatMap(this::uploadSingleImage)
                                        .map(uploadedImage -> UploadResult.success(uploadedImage.getFileMetadata().getFileName()))
                                        .doOnError(e -> log.error("Error processing file: {}",
                                                fileData.getFileMetadata().getOriginalFileName(), e))
                                        .onErrorResume(e -> Mono.just(
                                                UploadResult.failure(
                                                        fileData.getFileMetadata().getOriginalFileName(),
                                                        e.getMessage()
                                                )
                                        )), 5 // concurrency
                                ))
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
        return Mono.just(data)
                .flatMap(d -> {
                    String storageKey = buildStorageKey(d);

                    return storageService.uploadImage(storageKey,
                                    d.getBuffer(),
                                    d.getFileMetadata().getContentType())
                            .then(Mono.fromCallable(() -> buildImage(d, storageKey)));
                })
                .flatMap(this::saveImageWithMetadata)
                .onErrorMap(RuntimeException.class, e ->
                        new UploadException("Failed to upload " + data.getFileMetadata().getFileName(), e));
    }

    private Mono<ImageUploadData> validate(ImageUploadData data) {
        return Mono.fromCallable(() -> {
            for (ImageValidator v : validators) {
                v.validate(data);
            }
            return data;
        });
    }

    private Mono<Image> saveImageWithMetadata(Image image) {
        return imageRepository.save(image)
                .flatMap(savedEntity -> {
                    image.getFileMetadata().setImageId(savedEntity.getId());
                    image.getImageMetadata().setImageId(savedEntity.getId());

                    Mono<FileMetadata> savedFileMetadata = fileMetadataRepository.save(image.getFileMetadata());
                    Mono<ImageMetadata> savedImageMetadata = imageMetadataRepository.save(image.getImageMetadata());

                    return Mono.zip(savedFileMetadata, savedImageMetadata)
                            .map(tuple -> savedEntity);
                })
                .onErrorMap(e -> new RuntimeException("Failed to save image to database: "
                        + image.getFileMetadata().getOriginalFileName()));
    }

    private String buildStorageKey(ImageUploadData imageData) {
        return String.format("images/%s/%s/%s",
                Objects.toString(imageData.getUserId(), "null"),
                Objects.toString(imageData.getCameraId(), "null"),
                imageData.getFileMetadata().getFileName()
        );
    }

    private Image buildImage(ImageUploadData data, String imageKey) {
        String sourceMetadataJson = null;
        if (data.getSourceMetadata() != null && !data.getSourceMetadata().isEmpty()) {
            try {
                sourceMetadataJson = objectMapper.writeValueAsString(data.getSourceMetadata());
            } catch (Exception e) {
                log.warn("Failed to serialize source metadata: {}", e.getMessage());
                sourceMetadataJson = "{}";
            }
        }

        return Image.builder()
                .userId(data.getUserId())
                .cameraId(data.getCameraId())
                .sourceType(data.getSourceType())
                .sourceMetadata(sourceMetadataJson)
                .storageKey(imageKey)
                .imageMetadata(data.getImageMetadata())
                .fileMetadata(data.getFileMetadata())
                .build();
    }
}