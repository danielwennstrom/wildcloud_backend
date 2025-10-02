package org.wildcloud.wildcloud_backend.repository.RelationRepository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.wildcloud.wildcloud_backend.entity.RelationEntity.UserCamera;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserCameraRepository extends ReactiveCrudRepository<UserCamera,Long> {

    Flux<UserCamera> findByUserId(Long userId);
    Flux<UserCamera> findByCameraId(Long cameraId);
    Mono<Void> deleteByUserIdAndCameraId(Long userId, Long cameraId);
    Mono<Object> findByUserIdAndCameraId(Long userId, Long cameraId);


    Mono<Void> deleteByUserId(Long userId);
    Mono<Void> deleteByCameraId(Long cameraId);
}
