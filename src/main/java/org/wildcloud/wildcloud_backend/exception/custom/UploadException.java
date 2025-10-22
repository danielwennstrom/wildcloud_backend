package org.wildcloud.wildcloud_backend.exception.custom;

public class UploadException extends RuntimeException {
    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadException(String message) {
        super(message);
    }
}
