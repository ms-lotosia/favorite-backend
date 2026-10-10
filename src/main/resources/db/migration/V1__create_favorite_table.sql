CREATE TABLE favorites
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    product_id         BIGINT NOT NULL,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    CONSTRAINT uk_favorite_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_favorite_user_id ON favorites (user_id);
CREATE INDEX idx_favorite_product_id ON favorites (product_id);
