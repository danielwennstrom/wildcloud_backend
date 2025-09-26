CREATE TABLE IF NOT EXISTS public.camera_info
(
    id            SERIAL PRIMARY KEY,
    camera_email  VARCHAR(255) UNIQUE NOT NULL,

    camera_name   VARCHAR(255)

);