package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
