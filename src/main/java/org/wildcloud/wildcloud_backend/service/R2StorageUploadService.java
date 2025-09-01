package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.config.R2Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.concurrent.CompletableFuture;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class R2StorageUploadService implements StorageUploadService {
    private final S3Client r2Client;
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

    @Override
    public CompletableFuture<String> uploadImageAsync(String key, byte[] imageBytes, String contentType) {
        return null;
    }

    private String buildImageUrl(String key) {
        return String.format("https://%s.r2.cloudflarestorage.com/%s/%s",
                r2Properties.getAccountId(),
                r2Properties.getBucketName(),
                key);
    }
}
