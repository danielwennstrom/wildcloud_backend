package org.wildcloud.wildcloud_backend.exception.custom;

public class EmailTakenException extends RuntimeException {
    public EmailTakenException(String message) {
        super(message);
    }
}
