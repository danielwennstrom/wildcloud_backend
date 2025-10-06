package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.config.UploadConfig;
import org.wildcloud.wildcloud_backend.exception.custom.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

@Component
@Order(1)
public class FileSizeValidator implements ImageValidator {
    private final UploadConfig uploadConfig;

    public FileSizeValidator(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    @Override
    public void validate(ImageUploadData imageUploadData) throws ValidationException {
        if (imageUploadData.getBuffer().length > uploadConfig.getMaxFileSize().toBytes()) {
            throw new ValidationException("Image is too large: " + imageUploadData.getBuffer().length + " bytes");
        } else if (imageUploadData.getBuffer().length < uploadConfig.getMinFileSize().toBytes()) {
            throw new ValidationException("Image is too small: " + imageUploadData.getBuffer().length + " bytes");
        }
    }
}
