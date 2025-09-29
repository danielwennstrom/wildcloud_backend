package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;
import reactor.core.publisher.Mono;

public interface ImageRetrievalService {
    Mono<ImageResponseDto> retrieve(ImageEntity imageEntity);
}
