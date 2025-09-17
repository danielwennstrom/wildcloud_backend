package org.wildcloud.wildcloud_backend.service;

/**
 * An abstraction for a key-value blob storage system, responsible for persisting and retrieving image data.
 * The interface decouples the implemented storage technologies being used from the application.
 * The current implementations {@link R2StorageService} and {@link LocalStorageService} are activated
 * based on the running application profile (R2 for production, Local for development).
 */
public interface StorageService {
    /**
     *
     * @param key         The unique identifier value for the object to be persisted,
     *                    structured like a file path (image/userId/cameraId/example.png)
     * @param imageBytes  The binary content of the file
     * @param contentType The MIME type of the image ("image/jpeg", etc.),
     *                    which is used to set the Content-Type header when the image is served for proper presentation of the image
     * @return The key of the object pointing to the location of the
     * newly uploaded image
     */
    String uploadImage(String key, byte[] imageBytes, String contentType);
//    CompletableFuture<String> uploadImageAsync(String key, byte[] imageBytes, String contentType);

    /**
     *
     * @param key The unique identifier value for the object to be retrieved,
     *            structured like a file path (image/userId/cameraId/example.png)
     * @return A permanent file:// URI if used with the development application profile,
     * or a temporary signed URL if retireving an image from R2
     */
    String retrieveImage(String key);
}
