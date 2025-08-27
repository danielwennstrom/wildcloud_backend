package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.ImageValidator;

@Component
@Order(1)
public class FileSizeValidator implements ImageValidator {
    private final long minFileSize = 1024;
    private final long maxFileSize = 52428800;

    @Override
    public void validate(ImageUploadData imageData) throws ValidationException {
        if (imageData.getBuffer().length > maxFileSize) {
            throw new ValidationException("Image is too large: " + imageData.getBuffer().length + " bytes");
        } else if (imageData.getBuffer().length < minFileSize) {
            throw new ValidationException("Image is too small: " + imageData.getBuffer().length + " bytes");
        }
    }
}
