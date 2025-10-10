-- Additional constraints
-- ALTER TABLE images
--     ADD CONSTRAINT chk_source_type_valid
--         CHECK (source_type IN ('UPLOAD', 'CAMERA', 'API', 'BATCH'));

ALTER TABLE file_metadata
    ADD CONSTRAINT chk_file_name_not_empty
        CHECK (length(trim(file_name)) > 0);

ALTER TABLE file_metadata
    ADD CONSTRAINT chk_size_positive
        CHECK (size IS NULL OR size > 0);

-- Update timestamp triggers
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_images_updated_at
    BEFORE UPDATE
    ON images
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_file_metadata_updated_at
    BEFORE UPDATE
    ON file_metadata
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_image_metadata_updated_at
    BEFORE UPDATE
    ON image_metadata
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();