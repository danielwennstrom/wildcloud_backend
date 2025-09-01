package org.wildcloud.wildcloud_backend.service;

import java.util.concurrent.CompletableFuture;

public interface StorageUploadService {
    String uploadImage(String key, byte[] imageBytes, String contentType);

    CompletableFuture<String> uploadImageAsync(String key, byte[] imageBytes, String contentType);
}
