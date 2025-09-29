package org.wildcloud.wildcloud_backend.exception;

public class ImageRetrievalException extends RuntimeException {
    public ImageRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageRetrievalException(String message) {
        super(message);
    }
}
