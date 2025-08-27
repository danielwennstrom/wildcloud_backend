package org.wildcloud.wildcloud_backend.model;

import org.wildcloud.wildcloud_backend.exception.ValidationException;

public interface ImageValidator {
    void validate(ImageUploadData imageData) throws ValidationException;
}
