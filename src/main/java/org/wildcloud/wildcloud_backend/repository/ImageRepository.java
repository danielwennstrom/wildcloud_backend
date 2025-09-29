package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

public interface ImageRepository extends ReactiveCrudRepository<ImageEntity, Long> {
}
