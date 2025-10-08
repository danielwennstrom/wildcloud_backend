CREATE TABLE user_camera (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             camera_id BIGINT NOT NULL,
                             role VARCHAR(50),
                             camera_email VARCHAR(255),
                             assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
                             FOREIGN KEY (camera_id) REFERENCES camera_info(id) ON DELETE CASCADE,
                             UNIQUE(user_id, camera_id)
);