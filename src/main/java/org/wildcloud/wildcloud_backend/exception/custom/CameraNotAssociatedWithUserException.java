package org.wildcloud.wildcloud_backend.exception.custom;

public class CameraNotAssociatedWithUserException extends RuntimeException {
    public CameraNotAssociatedWithUserException(String message) {
        super(message);
    }
}

