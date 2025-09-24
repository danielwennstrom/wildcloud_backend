CREATE TABLE IF NOT EXISTS user_info
(
    id           SERIAL PRIMARY KEY,
    user_email   VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    phone_number BIGINT
);