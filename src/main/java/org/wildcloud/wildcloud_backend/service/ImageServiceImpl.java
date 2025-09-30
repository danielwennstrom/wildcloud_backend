package org.wildcloud.wildcloud_backend.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.repository.FileMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ImageMetadataRepository imageMetadataRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final StorageService storageService;

    // TODO: userId kanske ska användas till access control eller liknande?
    @Override
    public Flux<ImageResponseDto> retrieve(String userId, String cameraId, Pageable pageable) {
        return imageRepository.getAllByCameraId(cameraId, pageable)
                .collectList()
                .flatMapMany(images -> {
                    if (images.isEmpty()) {
                        return Flux.empty();
                    }

                    List<Long> imageIds = images.stream()
                            .map(Image::getId)
                            .collect(Collectors.toList());

                    Mono<List<ImageMetadata>> imageMetaList =
                            imageMetadataRepository.findByImageEntityIdIn(imageIds).collectList();
                    Mono<List<FileMetadata>> fileMetaList =
                            fileMetadataRepository.findByImageEntityIdIn(imageIds).collectList();

                    return Mono.zip(imageMetaList, fileMetaList)
                            .flatMapMany(tuple -> {
                                Map<Long, ImageMetadata> imageMetaMap = tuple.getT1().stream()
                                        .collect(Collectors.toMap(ImageMetadata::getImageEntityId, m -> m));
                                Map<Long, FileMetadata> fileMetaMap = tuple.getT2().stream()
                                        .collect(Collectors.toMap(FileMetadata::getImageEntityId, m -> m));

                                return Flux.fromIterable(images)
                                        .map(image -> new ImageWithMetadataHolder(
                                                image,
                                                imageMetaMap.get(image.getId()),
                                                fileMetaMap.get(image.getId())
                                        ))
                                        .flatMap(this::addPresignedUrlAndMapToDto, 10);
                            });
                });
    }

    @Override
    public Mono<Void> delete(String userId, Long imageId) {
        return imageRepository.findById(imageId)
                .switchIfEmpty(Mono.error(new RuntimeException("Image not found: " + imageId)))
                .flatMap(image ->
                        imageRepository.deleteById(image.getId())
                                .then(storageService.deleteImage(image.getStorageKey()))
                );
    }

    private Mono<ImageResponseDto> addPresignedUrlAndMapToDto(ImageWithMetadataHolder holder) {
        String storageKey = holder.image.getStorageKey();
        return Mono.fromCallable(() -> storageService.retrieveImage(storageKey))
                .map(url -> ImageResponseDto.builder()
                        .id(holder.image.getId())
                        .imageMetadata(holder.imageMetadata)
                        .fileMetadata(holder.fileMetadata)
                        .url(url)
                        .build());
    }

    @AllArgsConstructor
    private static class ImageWithMetadataHolder {
        private Image image;
        private ImageMetadata imageMetadata;
        private FileMetadata fileMetadata;
    }
}
