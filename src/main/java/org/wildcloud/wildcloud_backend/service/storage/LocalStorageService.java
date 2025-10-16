package org.wildcloud.wildcloud_backend.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.config.ImageRetrievalConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("development")
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService implements StorageService {
    private final ImageRetrievalConfig imageRetrievalConfig;
    
    @Override
    public Mono<Void> uploadImage(String key, byte[] imageData, String contentType) {
        return Mono.fromCallable(() -> {
                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    Path targetFile = uploadDir.resolve(key);
                    Files.createDirectories(targetFile.getParent());
                    Files.write(targetFile, imageData);

                    return targetFile.toUri().toString();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(response -> log.info("Successfully uploaded to local storage: {}", key))
                .onErrorMap(IOException.class, e ->
                        new RuntimeException("Failed to save image in local storage", e))
                .then();
    }

    @Override
    public String retrieveImage(String key) {
        return imageRetrievalConfig.getBaseUrl() + key.replace("\\", "/");
    }

    @Override
    public Mono<Void> deleteImage(String key) {
        return Mono.fromCallable(() -> {
                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    Path targetFile = uploadDir.resolve(key);

                    return Files.deleteIfExists(targetFile);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(v -> log.info("Successfully deleted image from local storage: {}", key))
                .onErrorMap(e -> new RuntimeException("Failed to delete image from local storage: " + key, e))
                .then();
    }
}

