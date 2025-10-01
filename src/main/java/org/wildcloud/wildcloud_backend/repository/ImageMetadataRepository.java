package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ImageMetadataRepository extends ReactiveCrudRepository<ImageMetadata, Long> {
    Flux<ImageMetadata> findByImageIdIn(List<Long> imageIds);
}
