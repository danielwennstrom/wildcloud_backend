package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;
import org.wildcloud.wildcloud_backend.exception.ImageRetrievalException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageRetrievalServiceImpl implements ImageRetrievalService {
    private final StorageService storageService;

    // TODO: ändra till dto + List ifall vi lika gärna kan hämta alla bilder på en gång
    @Override
    public Mono<ImageResponseDto> retrieve(ImageEntity imageEntity) {
        return Mono.fromCallable(() -> storageService.retrieveImage(imageEntity.getStorageKey()))
                .onErrorMap(RuntimeException.class, e ->
                        new ImageRetrievalException("Retrieval failed for the image: " + imageEntity.getStorageKey(), e))
                .map(presignedUrl -> new ImageResponseDto(
                        imageEntity.getId(),
                        presignedUrl,
                        Optional.ofNullable(imageEntity.getFileMetadata())
                                .map(FileMetadata::getOriginalFileName)
                                .orElse(null),
                        Optional.ofNullable(imageEntity.getFileMetadata())
                                .map(FileMetadata::getContentType)
                                .orElse("application/octet-stream"),
                        Optional.ofNullable(imageEntity.getFileMetadata())
                                .map(FileMetadata::getSize)
                                .orElse(0L)
                ));
    }
}
