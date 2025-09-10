package org.wildcloud.wildcloud_backend.exception.custom;

public class CameraNotFoundException extends RuntimeException {
    public CameraNotFoundException(String message) {
        super(message);
    }
}

