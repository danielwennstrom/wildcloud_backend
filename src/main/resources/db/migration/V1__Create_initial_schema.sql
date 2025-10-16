-- Create images table
CREATE TABLE images
(
    id              BIGSERIAL PRIMARY KEY,
    camera_id BIGINT NOT NULL,
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
    image_id BIGINT NOT NULL,
    file_name          VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    size               BIGINT,
    content_type       VARCHAR(100),
    created_at         TIMESTAMPTZ DEFAULT NOW(),
    updated_at         TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_file_metadata_image
        FOREIGN KEY (image_id)
            REFERENCES images (id)
            ON DELETE CASCADE
);

-- Create image_metadata table
CREATE TABLE image_metadata
(
    id              BIGSERIAL PRIMARY KEY,
    image_id BIGINT NOT NULL,
    captured_at     TIMESTAMPTZ,
    last_modified   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_image_metadata_image
        FOREIGN KEY (image_id)
            REFERENCES images (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_info
(
    id           SERIAL PRIMARY KEY,
    user_email   VARCHAR(255) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    phone_number BIGINT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes
CREATE INDEX idx_images_camera_id ON images (camera_id);
CREATE INDEX idx_images_source_type ON images (source_type);
CREATE INDEX idx_images_created_at ON images (created_at);
CREATE INDEX idx_images_storage_key ON images (storage_key);
-- Already unique, but good for lookups

-- Foreign key indexes (PostgreSQL doesn't auto-create these)
CREATE INDEX idx_file_metadata_image_id ON file_metadata (image_id);
CREATE INDEX idx_image_metadata_image_id ON image_metadata (image_id);

-- JSON indexes for metadata queries (if you plan to query JSON content)
CREATE INDEX idx_images_source_metadata ON images (source_metadata);

-- File metadata indexes
CREATE INDEX idx_file_metadata_content_type ON file_metadata (content_type);
CREATE INDEX idx_file_metadata_size ON file_metadata (size);

-- Image metadata indexes
CREATE INDEX idx_image_metadata_captured_at ON image_metadata (captured_at);

-- Comments for documentation
COMMENT ON TABLE images IS 'Main table for storing image entity information';
COMMENT ON TABLE file_metadata IS 'File-specific metadata for images';
COMMENT ON TABLE image_metadata IS 'Image-specific metadata like EXIF data';

COMMENT ON COLUMN images.source_metadata IS 'JSON metadata from the source (direct upload, email, etc.)';
COMMENT ON COLUMN images.storage_key IS 'Unique key for file storage location';