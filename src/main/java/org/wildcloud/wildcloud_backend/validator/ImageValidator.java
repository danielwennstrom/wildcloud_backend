package org.wildcloud.wildcloud_backend.validator;

import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

public interface ImageValidator {
    void validate(ImageUploadData imageUploadData) throws ValidationException;
}
