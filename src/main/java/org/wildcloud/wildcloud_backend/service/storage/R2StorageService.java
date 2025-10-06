package org.wildcloud.wildcloud_backend.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.config.R2Properties;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
@Profile("production")
@RequiredArgsConstructor
@Slf4j
public class R2StorageService implements StorageService {
    private final S3AsyncClient r2AsyncClient;
    private final S3Client r2Client;
    private final S3Presigner r2Presigner;
    private final R2Properties r2Properties;


    @Override
    public Mono<Void> uploadImage(String key, byte[] imageData, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();

        return Mono.fromFuture(() -> r2AsyncClient.putObject(request, AsyncRequestBody.fromBytes(imageData)))
                .doOnSuccess(response -> log.info("Successfully uploaded to R2: {}", key))
                .onErrorMap(e ->
                        new RuntimeException("Failed to upload image to R2: " + key, e))
                .then();
    }

    @Override
    public String retrieveImage(String key) {
        return generateSignedUrl(key, Duration.ofSeconds(300));
    }

    @Override
    public Mono<Void> deleteImage(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .build();

        return Mono.fromFuture(() -> r2AsyncClient.deleteObject(request))
                .doOnSuccess(v -> log.info("Successfully deleted image from R2: {}", key))
                .onErrorMap(e -> new RuntimeException("Failed to delete image from R2: " + key, e))
                .then();
    }

    private String generateSignedUrl(String imageKey, Duration expiration) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(imageKey)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = r2Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate signed URL for image: {}", imageKey, e);
            throw new RuntimeException("Signed URL generation failed", e);
        }
    }
}
