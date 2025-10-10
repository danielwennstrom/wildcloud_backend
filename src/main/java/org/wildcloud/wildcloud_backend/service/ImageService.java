package org.wildcloud.wildcloud_backend.service;

import org.springframework.data.domain.Pageable;
import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ImageService {
    Flux<ImageResponseDto> retrieve(String cameraId, Pageable pageable);

    Mono<Void> delete(Long imageId);
}
