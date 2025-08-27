package org.wildcloud.wildcloud_backend.validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.ValidationException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.ImageValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Order(2)
public class ContentTypeValidator implements ImageValidator {
    private static final String[] ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg", "gif", "bmp"};

    @Override
    public void validate(ImageUploadData imageData) throws ValidationException {
        checkContentType(imageData);
        checkFileExtension(imageData);
        checkImageContent(imageData);
    }

    private void checkContentType(ImageUploadData imageData) throws ValidationException {
        if (imageData.getContentType() == null || !imageData.getContentType().startsWith("image/")) {
            throw new ValidationException("Invalid content type: " + imageData.getContentType());
        }
    }

    private void checkFileExtension(ImageUploadData imageData) throws ValidationException {
        String fileName = imageData.getFilename();
        if (fileName == null || !fileName.matches("(?i).*\\.(" + String.join("|", ALLOWED_EXTENSIONS) + ")$")) {
            throw new ValidationException("Invalid file extension: " + fileName);
        }
    }

    private void checkImageContent(ImageUploadData imageData) throws ValidationException {
        try (InputStream is = new ByteArrayInputStream(imageData.getBuffer())) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new ValidationException("File is not a valid image");
            }
        } catch (IOException e) {
            throw new ValidationException("Failed to read image content", e);
        }
    }
}
