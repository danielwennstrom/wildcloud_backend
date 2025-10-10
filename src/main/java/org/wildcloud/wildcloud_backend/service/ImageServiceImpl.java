package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.mapper.ImageMapper;
import org.wildcloud.wildcloud_backend.repository.FileMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageMetadataRepository;
import org.wildcloud.wildcloud_backend.repository.ImageRepository;
import org.wildcloud.wildcloud_backend.service.storage.StorageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    private final StorageService storageService;
    private final ImageRepository imageRepository;
    private final ImageMetadataRepository imageMetadataRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final ImageMapper imageMapper;

    // TODO: userId kanske ska användas till access control eller liknande?
    @Override
    public Flux<ImageResponseDto> retrieve(String cameraId, Pageable pageable) {
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
                            imageMetadataRepository.findByImageIdIn(imageIds).collectList();
                    Mono<List<FileMetadata>> fileMetaList =
                            fileMetadataRepository.findByImageIdIn(imageIds).collectList();

                    return Mono.zip(imageMetaList, fileMetaList)
                            .flatMapMany(tuple -> {
                                Map<Long, ImageMetadata> imageMetaMap = tuple.getT1().stream()
                                        .collect(Collectors.toMap(ImageMetadata::getImageId, m -> m));
                                Map<Long, FileMetadata> fileMetaMap = tuple.getT2().stream()
                                        .collect(Collectors.toMap(FileMetadata::getImageId, m -> m));

                                return imageMapper.mapToDtos(images, imageMetaMap, fileMetaMap);
                            });
                });
    }

    @Override
    public Mono<Void> delete(Long imageId) {
        return imageRepository.findById(imageId)
                .switchIfEmpty(Mono.error(new RuntimeException("Image not found: " + imageId)))
                .flatMap(image ->
                        imageRepository.deleteById(image.getId())
                                .then(storageService.deleteImage(image.getStorageKey()))
                );
    }
}
