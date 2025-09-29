package org.wildcloud.wildcloud_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
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
    @Override
    public Mono<String> uploadImage(String key, byte[] imageData, String contentType) {
        return Mono.fromCallable(() -> {
                    Path uploadDir = Paths.get("uploads");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    Path targetFile = uploadDir.resolve(key);
                    Files.createDirectories(targetFile.getParent());
                    Files.write(targetFile, imageData);
                    log.info("Successfully uploaded to local: {}", key);

                    return targetFile.toUri().toString();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(IOException.class, e ->
                        new RuntimeException("Failed to save image locally", e));
    }

    @Override
    public String retrieveImage(String key) {
        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetFile = uploadDir.resolve(key);
            log.info("Local URL: {}", key);

            return targetFile.toUri().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find image locally", e);
        }
    }
}

