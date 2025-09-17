package org.wildcloud.wildcloud_backend.validator;

import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;

/**
 * Strategy interface for applying validation rules to processed image data via {@link ImageProcessor}.
 * Implementations must be annotated with {@code @Order} to apply the validators in a defined order,
 * as they are all currently implemented as a chain of responsibility.
 *
 * @see FileSizeValidator and
 * @see ContentTypeValidator for examples of implementations.
 */
public interface ImageValidator {
    void validate(ImageUploadData imageUploadData) throws ValidationException;
}
