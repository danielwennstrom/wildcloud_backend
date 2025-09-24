package org.wildcloud.wildcloud_backend.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepository extends  ReactiveCrudRepository <UserInfo, Long> {

    Mono<UserInfo> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<UserInfo> findByPhoneNumber(Long phoneNumber);
}
