package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileMetadataRepository extends ReactiveCrudRepository<FileMetadata, Long> {
    Flux<FileMetadata> findByImageEntityIdIn(List<Long> imageIds);

    @Modifying
    @Query("DELETE FROM file_metadata WHERE image_entity_id = :imageEntityId")
    Mono<Void> deleteByImageEntityId(Long imageEntityId);
}
