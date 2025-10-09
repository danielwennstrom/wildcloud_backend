package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.config.UploadConfig;
import org.wildcloud.wildcloud_backend.dto.UploadResultDto;
import org.wildcloud.wildcloud_backend.dto.UploadSummaryDto;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.custom.ProcessException;
import org.wildcloud.wildcloud_backend.exception.custom.UploadException;
import org.wildcloud.wildcloud_backend.mapper.ImageMapper;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.registrar.ProcessorRegistrar;
import org.wildcloud.wildcloud_backend.service.storage.StorageService;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {
    private final ProcessorRegistrar processorRegistrar;
    private final List<ImageValidator> validators;
    private final StorageService storageService;
    private final ImagePersistService imagePersistService;
    private final ImageMapper imageMapper;
    private final UploadConfig uploadConfig;
    // TODO: implementera events, kan användas till notifications etc.
    //    private final ApplicationEventPublisher eventPublisher;

    public Mono<UploadSummaryDto> processUpload(SourceType sourceType, ImageUploadContext context) {
        log.info("processUpload started for sourceType: {}", sourceType);
        ImageProcessor processor = processorRegistrar.getProcessor(sourceType);

        if (processor == null) {
            throw new ProcessException("No processor registered for source: " + sourceType);
        }

        return Mono.fromCallable(() -> processor.process(context))
                .flatMapMany(imageUploadDataList ->
                        Flux.fromIterable(imageUploadDataList)
                                .flatMap(imageUploadData -> validate(imageUploadData)
                                        .flatMap(this::uploadSingleImage)
                                        .map(uploadedImage -> UploadResultDto.success(uploadedImage.getFileMetadata().getFileName()))
                                        .doOnError(e -> log.error("Error processing file: {}",
                                                imageUploadData.getFileMetadata().getOriginalFileName(), e))
                                        .onErrorResume(e -> Mono.just(
                                                UploadResultDto.failure(
                                                        imageUploadData.getFileMetadata().getOriginalFileName(),
                                                        e.getMessage()
                                                )
                                        )), uploadConfig.getConcurrencyLimit() // concurrency
                                ))
                .collectList()
                .map(results -> {
                    Map<Boolean, List<UploadResultDto>> partitioned = results.stream()
                            .collect(Collectors.partitioningBy(UploadResultDto::isSuccess));

                    return UploadSummaryDto.builder()
                            .successes(partitioned.get(true))
                            .failures(partitioned.get(false))
                            .build();
                })
                .onErrorMap(e -> new UploadException("Upload failed", e));
    }

    public Mono<Image> uploadSingleImage(ImageUploadData data) {
        return Mono.just(data)
                .flatMap(d -> {
                    String storageKey = imageMapper.buildStorageKey(d);

                    return storageService.uploadImage(storageKey,
                                    d.getBuffer(),
                                    d.getFileMetadata().getContentType())
                            .then(Mono.fromCallable(() -> imageMapper.buildImage(d, storageKey)));
                })
                .flatMap(imagePersistService::saveImageWithMetadata)
                .onErrorMap(RuntimeException.class, e ->
                        new UploadException("Failed to upload: " + data.getFileMetadata().getFileName(), e));
    }

    private Mono<ImageUploadData> validate(ImageUploadData data) {
        return Mono.fromCallable(() -> {
            for (ImageValidator v : validators) {
                v.validate(data);
            }
            return data;
        });
    }
}