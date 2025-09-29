-- Create images table
CREATE TABLE images
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         VARCHAR(255) NOT NULL,
    camera_id       VARCHAR(255),
    source_type     VARCHAR(100) NOT NULL,
    source_metadata TEXT,
    storage_key     VARCHAR(500) NOT NULL UNIQUE,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Create file_metadata table
CREATE TABLE file_metadata
(
    id                 BIGSERIAL PRIMARY KEY,
    image_entity_id    BIGINT       NOT NULL,
    file_name          VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    size               BIGINT,
    content_type       VARCHAR(100),
    created_at         TIMESTAMPTZ DEFAULT NOW(),
    updated_at         TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_file_metadata_image
        FOREIGN KEY (image_entity_id)
            REFERENCES images (id)
            ON DELETE CASCADE
);

-- Create image_metadata table
CREATE TABLE image_metadata
(
    id              BIGSERIAL PRIMARY KEY,
    image_entity_id BIGINT NOT NULL,
    captured_at     TIMESTAMPTZ,
    last_modified   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_image_metadata_image
        FOREIGN KEY (image_entity_id)
            REFERENCES images (id)
            ON DELETE CASCADE
);

-- Comments for documentation
COMMENT ON TABLE images IS 'Main table for storing image entity information';
COMMENT ON TABLE file_metadata IS 'File-specific metadata for images';
COMMENT ON TABLE image_metadata IS 'Image-specific metadata like EXIF data';

COMMENT ON COLUMN images.source_metadata IS 'JSON metadata from the source (direct upload, email, etc.)';
COMMENT ON COLUMN images.storage_key IS 'Unique key for file storage location';