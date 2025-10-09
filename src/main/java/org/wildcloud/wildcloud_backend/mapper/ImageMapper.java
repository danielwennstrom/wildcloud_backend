package org.wildcloud.wildcloud_backend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.config.ImageRetrievalConfig;
import org.wildcloud.wildcloud_backend.dto.FileMetadataDto;
import org.wildcloud.wildcloud_backend.dto.ImageMetadataDto;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.service.storage.StorageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageMapper {
    private final StorageService storageService;
    private final ImageRetrievalConfig imageRetrievalConfig;
    private final ObjectMapper objectMapper;

    public Mono<ImageResponseDto> mapToDto(Image image,
                                           ImageMetadata imageMetadata,
                                           FileMetadata fileMetadata) {
        return Mono.fromCallable(() -> storageService.retrieveImage(image.getStorageKey()))
                .map(url -> ImageResponseDto.builder()
                        .id(image.getId())
                        .imageMetadata(toImageMetadataDto(imageMetadata))
                        .fileMetadata(toFileMetadataDto(fileMetadata))
                        .url(url)
                        .build());
    }

    public Flux<ImageResponseDto> mapToDtos(List<Image> images,
                                            Map<Long, ImageMetadata> imageMetaMap,
                                            Map<Long, FileMetadata> fileMetaMap) {
        return Flux.fromIterable(images)
                .flatMap(image -> mapToDto(
                        image,
                        imageMetaMap.get(image.getId()),
                        fileMetaMap.get(image.getId())
                ), imageRetrievalConfig.getConcurrencyLimit());
    }

    public String buildStorageKey(ImageUploadData imageData) {
        return String.format("images/%s/%s/%s",
                Objects.toString(imageData.getUserId(), "null"),
                Objects.toString(imageData.getCameraId(), "null"),
                imageData.getFileMetadata().getFileName()
        );
    }

    public Image buildImage(ImageUploadData data, String imageKey) {
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

    private ImageMetadataDto toImageMetadataDto(ImageMetadata entity) {
        if (entity == null) return null;
        return ImageMetadataDto.builder()
                .capturedAt(entity.getCapturedAt())
                .lastModified(entity.getLastModified())
                .build();
    }

    private FileMetadataDto toFileMetadataDto(FileMetadata entity) {
        if (entity == null) return null;
        return FileMetadataDto.builder()
                .fileName(entity.getFileName())
                .originalFileName(entity.getOriginalFileName())
                .size(entity.getSize())
                .contentType(entity.getContentType())
                .build();
    }
}
