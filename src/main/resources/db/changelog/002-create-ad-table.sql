CREATE TABLE IF NOT EXISTS ads (
    id BIGSERIAL     PRIMARY KEY,
    offer_type       VARCHAR(16)   NOT NULL,
    title            VARCHAR(128)  NOT NULL,
    category_name    VARCHAR(32)   NOT NULL,
    subcategory_name VARCHAR(32)   NOT NULL,
    description      VARCHAR(1024) NOT NULL,
    price            INTEGER       NOT NULL CHECK (price > 0),
    views            INTEGER   DEFAULT 0,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    republished_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);






