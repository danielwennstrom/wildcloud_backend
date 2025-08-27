package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {
    private final Map<String, ImageProcessor> processors = new ConcurrentHashMap<>();
    private final List<ImageValidator> validators;
//    private final StorageService storageService;
//    private final MetadataService metadataService;
//    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UploadResult processUpload(String sourceType, Object inputData) throws UploadException {
        try {
            ImageProcessor processor = processors.get(sourceType);
            if (processor == null) {
                throw new UploadException("No processor registered for source: " + sourceType);
            }

            ImageUploadData imageData = processor.process(inputData);
            for (ImageValidator validator : validators) {
                validator.validate(imageData.getFileMetadata());
            }

            UploadResult result = uploadImage(imageData);

            return result;
        } catch (Exception e) {
            throw new UploadException("Upload failed", e);
        }
    }

    @Override
    public UploadResult uploadImage(ImageUploadData imageData) {
        ImageEntity imageEntity = ImageEntity.builder()
                .userId(imageData.getUserId())
                .cameraId(imageData.getCameraId())
                .fileName(imageData.getFileMetadata().getFileName())
                .fileSize((long) imageData.getFileMetadata().getBuffer().length)
                .contentType(imageData.getFileMetadata().getContentType())
                .sourceType(imageData.getSourceType())
                .sourceMetadata(imageData.getSourceMetadata())
                .capturedAt(imageData.getImageMetadata().getCapturedAt())
                .uploadedAt(LocalDateTime.now())
                .build();

        System.out.println(imageData);
        return UploadResult.builder()
                .id(imageEntity.getId())
                .url("test")
                .metadata(imageEntity)
                .build();
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
}
