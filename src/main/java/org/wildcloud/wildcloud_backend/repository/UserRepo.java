package org.wildcloud.wildcloud_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.UserInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserInfo> findById(Long UserId);
    List<UserInfo> findAll();
    Optional<UserInfo> findAllCamerasByUserId(Long userId);


}
