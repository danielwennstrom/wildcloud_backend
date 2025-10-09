package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UploadResultDto {
    private final String fileName;
    private final boolean success;
    private final String errorMessage;

    private UploadResultDto(String fileName, boolean success, String errorMessage) {
        this.fileName = fileName;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static UploadResultDto success(String fileName) {
        return new UploadResultDto(fileName, true, null);
    }

    public static UploadResultDto failure(String fileName, String errorMessage) {
        return new UploadResultDto(fileName, false, errorMessage);
    }
}
