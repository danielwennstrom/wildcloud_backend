package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.FileMetadata;

@Component
@Order(1)
public class FileSizeValidator implements ImageValidator {
    private final long minFileSize = 1024;
    private final long maxFileSize = 52428800;

    @Override
    public void validate(FileMetadata fileMetadata) throws ValidationException {
        if (fileMetadata.getBuffer().length > maxFileSize) {
            throw new ValidationException("Image is too large: " + fileMetadata.getBuffer().length + " bytes");
        } else if (fileMetadata.getBuffer().length < minFileSize) {
            throw new ValidationException("Image is too small: " + fileMetadata.getBuffer().length + " bytes");
        }
    }
}
