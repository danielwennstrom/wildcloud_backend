package org.wildcloud.wildcloud_backend.processor;

import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.custom.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

import java.util.List;

/**
 * Strategy interface for processing image uploads from various implemented sources.
 * Responsible for taking input data from implemented sources, extracting image bytes and metadata,
 * and converting it into a standardized format, {@link ImageUploadData}.
 * Don't forget to add an enum to {@link SourceType}
 * @see DirectUploadProcessor for an example implementation.
 */
public interface ImageProcessor {
    List<ImageUploadData> process(ImageUploadContext context) throws ProcessException;

    SourceType getSourceType();
}
