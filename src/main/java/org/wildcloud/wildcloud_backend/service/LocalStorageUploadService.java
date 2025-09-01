package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class LocalStorageUploadService implements StorageUploadService {

    @Override
    public String uploadImage(String key, byte[] imageData, String contentType) {
        try {
            Path uploadDir = Paths.get("./uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetFile = uploadDir.resolve(key);
            Files.createDirectories(targetFile.getParent());
            Files.write(targetFile, imageData);
            log.info("Successfully uploaded to local: {}", key);

            return targetFile.toUri().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image locally", e);
        }
    }

    @Override
    public CompletableFuture<String> uploadImageAsync(String key, byte[] imageBytes, String contentType) {
        return null;
    }
}

