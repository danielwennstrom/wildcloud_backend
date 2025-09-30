package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.Image;
import reactor.core.publisher.Flux;

public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {
    Flux<Image> getAllByCameraId(String cameraId, Pageable pageable);
}
