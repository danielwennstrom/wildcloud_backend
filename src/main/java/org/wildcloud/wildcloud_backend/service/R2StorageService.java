package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.config.R2Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
    private final S3Client r2Client;
    private final S3Presigner r2Presigner;
    // TODO: implementera async
    //    private final S3AsyncClient r2AsyncClient;
    //    private final S3TransferManager r2TransferManager;
    private final R2Properties r2Properties;


    @Override
    public String uploadImage(String key, byte[] imageData, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .build();

            r2Client.putObject(request, RequestBody.fromBytes(imageData));
            log.info("Successfully uploaded to R2: {}", key);

            return buildImageUrl(key);
        } catch (Exception e) {
            log.error("Failed to upload image to R2: {}", key, e);
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public CompletableFuture<String> uploadImageAsync(String key, byte[] imageBytes, String contentType) {
//        return null;
//    }

    @Override
    public String retrieveImage(String key) {
        return generateSignedUrl(key, Duration.ofSeconds(300));
    }

    private String buildImageUrl(String key) {
        return String.format("https://%s.r2.cloudflarestorage.com/%s/%s",
                r2Properties.getAccountId(),
                r2Properties.getBucketName(),
                key);
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
