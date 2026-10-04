-- ============================================================
-- 006_reviews.sql  —  Reviews & Recommendations
-- ============================================================

-- Reviews
CREATE TABLE reviews (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    book_id     UUID          NOT NULL,
    rating      INTEGER       NOT NULL,
    comment     TEXT,
    status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    version     INTEGER       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_reviews_user  FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reviews_book  FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT uq_reviews UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reviews_book_id ON reviews (book_id);

-- Recommendations
CREATE TABLE recommendations (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID           NOT NULL,
    book_id     UUID           NOT NULL,
    score       NUMERIC(5,4)   NOT NULL,
    reason      VARCHAR(255),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_recommendations_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_recommendations_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT uq_recommendations UNIQUE (user_id, book_id)
);

CREATE INDEX idx_recommendations_user_id ON recommendations (user_id);
