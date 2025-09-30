package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

public interface ImageMetadataRepository extends ReactiveCrudRepository<ImageMetadata, Long> {
    Flux<ImageMetadata> findByImageEntityIdIn(List<Long> imageIds);

    @Modifying
    @Query("DELETE FROM image_metadata WHERE image_entity_id = :imageEntityId")
    Mono<Void> deleteByImageEntityId(Long imageEntityId);

    Flux<ImageMetadata> findByCapturedAtBetween(OffsetDateTime start, OffsetDateTime end);
}
