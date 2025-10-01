package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import reactor.core.publisher.Flux;

import java.util.List;

public interface FileMetadataRepository extends ReactiveCrudRepository<FileMetadata, Long> {
    Flux<FileMetadata> findByImageIdIn(List<Long> imageIds);
}
