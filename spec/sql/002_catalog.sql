-- ============================================================
-- 002_catalog.sql  —  Catalog: publishers, authors, categories,
--                      books, book_authors, book_categories
-- ============================================================

-- Publishers
CREATE TABLE publishers (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255)  NOT NULL,
    country      VARCHAR(100),
    website_url  VARCHAR(500),
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_publishers_name UNIQUE (name)
);

-- Authors
CREATE TABLE authors (
    id                 UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name         VARCHAR(100)  NOT NULL,
    last_name          VARCHAR(100)  NOT NULL,
    bio                TEXT,
    profile_image_url  VARCHAR(500),
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by         VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by         VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted         BOOLEAN       NOT NULL DEFAULT FALSE
);

-- Categories (self-referential for hierarchy)
CREATE TABLE categories (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(100)  NOT NULL,
    slug           VARCHAR(100)  NOT NULL,
    parent_id      UUID,
    display_order  INTEGER       NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
);

-- Books
CREATE TABLE books (
    id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    title            VARCHAR(500)   NOT NULL,
    isbn             VARCHAR(20)    NOT NULL,
    publisher_id     UUID           NOT NULL,
    description      TEXT,
    price            NUMERIC(12,2)  NOT NULL,
    stock_quantity   INTEGER        NOT NULL DEFAULT 0,
    cover_image_url  VARCHAR(500),
    language         VARCHAR(50)    NOT NULL DEFAULT 'English',
    pages            INTEGER,
    published_date   DATE,
    delivery_days    INTEGER        NOT NULL DEFAULT 5,
    average_rating   NUMERIC(3,2)   DEFAULT 0.00,
    review_count     INTEGER        DEFAULT 0,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    version          INTEGER        NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by       VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by       VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted       BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_books_isbn UNIQUE (isbn),
    CONSTRAINT chk_books_price CHECK (price >= 0),
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers (id)
);

CREATE INDEX idx_books_isbn         ON books (isbn);
CREATE INDEX idx_books_publisher_id ON books (publisher_id);
CREATE INDEX idx_books_title_fts    ON books USING GIN (to_tsvector('english', title));

-- Book ↔ Author (many-to-many)
CREATE TABLE book_authors (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id     UUID          NOT NULL,
    author_id   UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_book_authors_book   FOREIGN KEY (book_id)   REFERENCES books (id),
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors (id),
    CONSTRAINT uq_book_authors UNIQUE (book_id, author_id)
);

-- Book ↔ Category (many-to-many)
CREATE TABLE book_categories (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id      UUID          NOT NULL,
    category_id  UUID          NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_book_categories_book     FOREIGN KEY (book_id)     REFERENCES books (id),
    CONSTRAINT fk_book_categories_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uq_book_categories UNIQUE (book_id, category_id)
);
