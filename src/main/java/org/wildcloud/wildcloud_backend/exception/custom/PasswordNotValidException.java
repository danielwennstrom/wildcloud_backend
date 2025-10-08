package org.wildcloud.wildcloud_backend.exception.custom;

public class PasswordNotValidException extends RuntimeException {
    public PasswordNotValidException(String message) {
        super(message);
    }
}
