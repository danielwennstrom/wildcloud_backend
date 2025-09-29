-- Performance indexes
CREATE INDEX idx_images_user_id ON images (user_id);
CREATE INDEX idx_images_camera_id ON images (camera_id);
CREATE INDEX idx_images_source_type ON images (source_type);
CREATE INDEX idx_images_created_at ON images (created_at);
CREATE INDEX idx_images_storage_key ON images (storage_key);
-- Already unique, but good for lookups

-- Foreign key indexes (PostgreSQL doesn't auto-create these)
CREATE INDEX idx_file_metadata_image_id ON file_metadata (image_entity_id);
CREATE INDEX idx_image_metadata_image_id ON image_metadata (image_entity_id);

-- Composite indexes for common query patterns
CREATE INDEX idx_images_user_camera ON images (user_id, camera_id);
CREATE INDEX idx_images_user_created ON images (user_id, created_at DESC);

-- JSON indexes for metadata queries (if you plan to query JSON content)
CREATE INDEX idx_images_source_metadata ON images (source_metadata);

-- File metadata indexes
CREATE INDEX idx_file_metadata_content_type ON file_metadata (content_type);
CREATE INDEX idx_file_metadata_size ON file_metadata (size);

-- Image metadata indexes
CREATE INDEX idx_image_metadata_captured_at ON image_metadata (captured_at);