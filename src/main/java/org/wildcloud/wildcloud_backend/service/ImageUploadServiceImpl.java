package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.repository.ImageRepository;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;

import java.util.ArrayList;
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
    private final StorageUploadService storageUploadService;
    // TODO: implementera events, kan användas till notifications etc.
//    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UploadResult processUpload(String sourceType, Object inputData) throws UploadException {
        ImageProcessor processor = processors.get(sourceType);
        if (processor == null) {
            throw new UploadException("No processor registered for source: " + sourceType);
        }

        try {
            List<ImageUploadData> imageData = processor.process(inputData);

            imageData.forEach(data ->
                    validators.forEach(v -> {
                        try {
                            v.validate(data);
                        } catch (ValidationException e) {
                            throw new RuntimeException(e);
                        }
                    })
            );

            return uploadImages(imageData);
        } catch (
                Exception e) {
            throw new UploadException("Upload failed", e);
        }
    }

    @Override
    public UploadResult uploadImages(List<ImageUploadData> imageDataList) {
        List<ImageEntity> imageEntityList = new ArrayList<>();

        for (ImageUploadData data : imageDataList) {
            String imageKey = buildImageKey(data);
            storageUploadService.uploadImage(imageKey, data.getBuffer(), data.getFileMetadata().getContentType());

            ImageEntity imageEntity = ImageEntity.builder()
                    .userId(data.getUserId())
                    .cameraId(data.getCameraId())
                    .sourceType(data.getSourceType())
                    .sourceMetadata(data.getSourceMetadata())
                    .imageMetadata(data.getImageMetadata())
                    .fileMetadata(data.getFileMetadata())
                    .storageKey(imageKey)
                    .build();

            setEntityAssociations(data, imageEntity);

            imageEntityList.add(imageEntity);
            imageRepository.save(imageEntity);
        }

        return UploadResult.builder()
                .uploadedCount(imageEntityList.size())
                .metadataList(imageEntityList)
                .build();
    }

    private void setEntityAssociations(ImageUploadData data, ImageEntity imageEntity) {
        if (data.getImageMetadata() != null) {
            data.getImageMetadata().setImageEntity(imageEntity);
            imageEntity.setImageMetadata(data.getImageMetadata());
        }

        if (data.getFileMetadata() != null) {
            data.getFileMetadata().setImageEntity(imageEntity);
            imageEntity.setFileMetadata(data.getFileMetadata());
        }
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
}
