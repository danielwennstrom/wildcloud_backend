package org.wildcloud.wildcloud_backend.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserInfo, Long> {

    Mono<UserInfo> findByUserEmail(String userEmail);

    Mono<Boolean> existsByUserEmail(String userEmail);

    Mono<UserInfo> findByPhoneNumber(Long phoneNumber);

}

