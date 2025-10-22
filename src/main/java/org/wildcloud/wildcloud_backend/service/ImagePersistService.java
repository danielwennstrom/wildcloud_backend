package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.repository.FileMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ImagePersistService {
    private final ImageRepository imageRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final ImageMetadataRepository imageMetadataRepository;

    public Mono<Image> saveImageWithMetadata(Image image) {
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
}
