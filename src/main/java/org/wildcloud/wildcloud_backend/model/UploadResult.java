package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadResult {
    private final String fileName;
    private final boolean success;
    private final String errorMessage;

    private UploadResult(String fileName, boolean success, String errorMessage) {
        this.fileName = fileName;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static UploadResult success(String fileName) {
        return new UploadResult(fileName, true, null);
    }

    public static UploadResult failure(String fileName, String errorMessage) {
        return new UploadResult(fileName, false, errorMessage);
    }
}
