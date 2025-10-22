package org.wildcloud.wildcloud_backend.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

@Component
public class ExifUtils {
    public OffsetDateTime getOriginalOffsetDateTime(InputStream is) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(is);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (directory == null) return null;

        Date originalDate = directory.getDateOriginal(TimeZone.getDefault());
        return getOffsetDateTime(originalDate, directory);
    }

    public OffsetDateTime getModifiedOffsetDateTime(InputStream is) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(is);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (directory == null) return null;
        
        Date modifiedDate = directory.getDateModified(TimeZone.getDefault());
        return getOffsetDateTime(modifiedDate, directory);
    }

    private OffsetDateTime getOffsetDateTime(Date date, ExifSubIFDDirectory directory) {
        if (date == null) return null;

        String offsetStr = directory.getString(ExifSubIFDDirectory.TAG_TIME_ZONE_ORIGINAL);

        ZoneOffset offset;
        if (offsetStr != null && !offsetStr.isEmpty()) {
            offset = ZoneOffset.of(offsetStr); // e.g. "-08:00"
        } else {
            offset = ZoneOffset.systemDefault().getRules().getOffset(date.toInstant());
        }

        return OffsetDateTime.ofInstant(date.toInstant(), offset);
    }
}
