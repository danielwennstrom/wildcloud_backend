package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.FileMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Order(2)
public class ContentTypeValidator implements ImageValidator {
    private static final String[] ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg"};

    @Override
    public void validate(FileMetadata fileMetadata) throws ValidationException {
        checkContentType(fileMetadata);
        checkFileExtension(fileMetadata);
        checkImageContent(fileMetadata);
    }

    private void checkContentType(FileMetadata fileMetadata) throws ValidationException {
        if (fileMetadata.getContentType() == null || !fileMetadata.getContentType().startsWith("image/")) {
            throw new ValidationException("Invalid content type: " + fileMetadata.getContentType());
        }
    }

    private void checkFileExtension(FileMetadata fileMetadata) throws ValidationException {
        String fileName = fileMetadata.getFileName();
        if (fileName == null || !fileName.matches("(?i).*\\.(" + String.join("|", ALLOWED_EXTENSIONS) + ")$")) {
            throw new ValidationException("Invalid file extension: " + fileName);
        }
    }

    private void checkImageContent(FileMetadata fileMetadata) throws ValidationException {
        try (InputStream is = new ByteArrayInputStream(fileMetadata.getBuffer())) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new ValidationException("File is not a valid image");
            }
        } catch (IOException e) {
            throw new ValidationException("Failed to read image content", e);
        }
    }
}
