package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

@Service
@RequiredArgsConstructor
public class ImageRetrievalServiceImpl implements ImageRetrievalService {
    private final StorageService storageService;

    // TODO: ändra till dto + List ifall vi lika gärna kan hämta alla bilder på en gång
    @Override
    public ImageResponseDto retrieve(ImageEntity imageEntity) {
        String presignedUrl = storageService.retrieveImage(imageEntity.getStorageKey());
        return new ImageResponseDto(
                imageEntity.getId(),
                presignedUrl,
                imageEntity.getFileMetadata().getOriginalFileName(),
                imageEntity.getFileMetadata().getContentType(),
                imageEntity.getFileMetadata().getSize()
        );
    }
}
