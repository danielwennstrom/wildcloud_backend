package org.wildcloud.wildcloud_backend.repository;

import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.entity.UserInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserInfo> findById(Long id);
    List<UserInfo> findAll();
    Optional<CameraInfo> findAllCamerasById(Long id);

    Optional<UserInfo> findByPhoneNumber(Long phoneNumber);

}
