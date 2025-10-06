package org.wildcloud.wildcloud_backend.exception.custom;

public class UserAlreadyOwnsException extends RuntimeException {
    public UserAlreadyOwnsException(String message) {
        super(message);
    }
}
