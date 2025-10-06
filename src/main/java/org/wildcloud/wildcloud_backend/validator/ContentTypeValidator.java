package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.custom.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class ContentTypeValidator implements ImageValidator {
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    @Override
    public void validate(ImageUploadData data) throws ValidationException {
        validateContentTypeHeader(data);
        validateFileExtension(data);
        validateFileMagicNumbers(data);
    }

    private void validateContentTypeHeader(ImageUploadData data) {
        String contentType = data.getFileMetadata().getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException("Unsupported or invalid content type: " + contentType);
        }
    }

    private void validateFileExtension(ImageUploadData data) {
        String fileName = data.getFileMetadata().getFileName();
        if (fileName == null || !fileName.matches("(?i).*\\.(jpg|jpeg|png|webp)$")) {
            throw new ValidationException("Unsupported or invalid file extension: " + fileName);
        }
    }

    private void validateFileMagicNumbers(ImageUploadData data) {
        byte[] buffer = data.getBuffer();
        if (buffer.length < 4) {
            throw new ValidationException("File too small to be valid");
        }

        if (!hasValidImageMagicNumber(buffer)) {
            throw new ValidationException("File content doesn't match the expected image format");
        }
    }

    private boolean hasValidImageMagicNumber(byte[] buffer) {
        // JPEG: FF D8 FF
        if (buffer.length >= 3 &&
                (buffer[0] & 0xFF) == 0xFF &&
                (buffer[1] & 0xFF) == 0xD8 &&
                (buffer[2] & 0xFF) == 0xFF) {
            return true;
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (buffer.length >= 8 &&
                (buffer[0] & 0xFF) == 0x89 &&
                (buffer[1] & 0xFF) == 0x50 &&
                (buffer[2] & 0xFF) == 0x4E &&
                (buffer[3] & 0xFF) == 0x47) {
            return true;
        }

        // WebP: 52 49 46 46 ... 57 45 42 50
        return buffer.length >= 12 &&
                (buffer[0] & 0xFF) == 0x52 &&
                (buffer[1] & 0xFF) == 0x49 &&
                (buffer[2] & 0xFF) == 0x46 &&
                (buffer[3] & 0xFF) == 0x46 &&
                (buffer[8] & 0xFF) == 0x57 &&
                (buffer[9] & 0xFF) == 0x45 &&
                (buffer[10] & 0xFF) == 0x42 &&
                (buffer[11] & 0xFF) == 0x50;
    }
}
