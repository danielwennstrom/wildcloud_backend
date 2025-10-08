package org.wildcloud.wildcloud_backend.exception.custom;

public class EmailInvalidFormatException extends RuntimeException {
    public EmailInvalidFormatException(String message) {
        super(message);
    }
}
