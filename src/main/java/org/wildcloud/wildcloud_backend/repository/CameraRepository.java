package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CameraRepository  extends JpaRepository<CameraInfo, String> {

    Optional<CameraInfo> findByEmail(String email);
    boolean existsByEmail(String email);
    List<CameraInfo> findAll();


}
