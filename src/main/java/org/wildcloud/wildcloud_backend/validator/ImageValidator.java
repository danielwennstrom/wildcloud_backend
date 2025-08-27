package org.wildcloud.wildcloud_backend.validator;

import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.FileMetadata;

public interface ImageValidator {
    void validate(FileMetadata fileMetadata) throws ValidationException;
}
