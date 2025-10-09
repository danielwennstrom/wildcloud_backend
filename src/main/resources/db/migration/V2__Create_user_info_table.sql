CREATE TABLE IF NOT EXISTS public.user_info
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