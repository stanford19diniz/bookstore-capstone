-- =====================================================================
-- V1__initial_schema.sql
-- Full bookstore schema — 25 tables across 8 domains
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── ROLES ──────────────────────────────────────────────────────────
CREATE TABLE roles (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50)   NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    version     INTEGER       NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- ── USERS ──────────────────────────────────────────────────────────
CREATE TABLE users (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    first_name     VARCHAR(100)  NOT NULL,
    last_name      VARCHAR(100)  NOT NULL,
    phone          VARCHAR(20),
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    version        INTEGER       NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);

-- ── USER_ROLES ─────────────────────────────────────────────────────
CREATE TABLE user_roles (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    role_id     UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    version     INTEGER       NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT uq_user_roles UNIQUE (user_id, role_id)
);

-- ── ADDRESSES ──────────────────────────────────────────────────────
CREATE TABLE addresses (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    label       VARCHAR(50),
    line1       VARCHAR(255)  NOT NULL,
    line2       VARCHAR(255),
    city        VARCHAR(100)  NOT NULL,
    state       VARCHAR(100)  NOT NULL,
    country     VARCHAR(100)  NOT NULL,
    pincode     VARCHAR(20)   NOT NULL,
    is_default  BOOLEAN       NOT NULL DEFAULT FALSE,
    version     INTEGER       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);

-- ── PUBLISHERS ─────────────────────────────────────────────────────
CREATE TABLE publishers (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255)  NOT NULL,
    country      VARCHAR(100),
    website_url  VARCHAR(500),
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    version      INTEGER       NOT NULL DEFAULT 0,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_publishers_name UNIQUE (name)
);

-- ── AUTHORS ────────────────────────────────────────────────────────
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
    version            INTEGER       NOT NULL DEFAULT 0,
    is_deleted         BOOLEAN       NOT NULL DEFAULT FALSE
);

-- ── CATEGORIES ─────────────────────────────────────────────────────
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
    version        INTEGER       NOT NULL DEFAULT 0,
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_categories_slug   UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
);

-- ── BOOKS ──────────────────────────────────────────────────────────
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
    CONSTRAINT uq_books_isbn      UNIQUE (isbn),
    CONSTRAINT chk_books_price    CHECK (price >= 0),
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES publishers (id)
);

CREATE INDEX idx_books_isbn         ON books (isbn);
CREATE INDEX idx_books_publisher_id ON books (publisher_id);
CREATE INDEX idx_books_title_fts    ON books USING GIN (to_tsvector('english', title));

-- ── BOOK_AUTHORS ───────────────────────────────────────────────────
CREATE TABLE book_authors (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id     UUID          NOT NULL,
    author_id   UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    version     INTEGER       NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_book_authors_book   FOREIGN KEY (book_id)   REFERENCES books (id),
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors (id),
    CONSTRAINT uq_book_authors        UNIQUE (book_id, author_id)
);

-- ── BOOK_CATEGORIES ────────────────────────────────────────────────
CREATE TABLE book_categories (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id      UUID          NOT NULL,
    category_id  UUID          NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    version      INTEGER       NOT NULL DEFAULT 0,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_book_categories_book     FOREIGN KEY (book_id)     REFERENCES books (id),
    CONSTRAINT fk_book_categories_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT uq_book_categories          UNIQUE (book_id, category_id)
);

-- ── COUPONS ────────────────────────────────────────────────────────
CREATE TABLE coupons (
    id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    code             VARCHAR(50)    NOT NULL,
    description      TEXT,
    discount_type    VARCHAR(20)    NOT NULL,
    discount_value   NUMERIC(12,2)  NOT NULL,
    min_order_value  NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    max_uses         INTEGER,
    used_count       INTEGER        NOT NULL DEFAULT 0,
    expires_at       TIMESTAMPTZ,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    version          INTEGER        NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by       VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by       VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted       BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_coupons_code   UNIQUE (code),
    CONSTRAINT chk_coupons_type  CHECK (discount_type IN ('PERCENTAGE', 'FIXED')),
    CONSTRAINT chk_coupons_value CHECK (discount_value > 0)
);

-- ── WISHLISTS ──────────────────────────────────────────────────────
CREATE TABLE wishlists (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    version     INTEGER       NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wishlists_user  FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_wishlists_user  UNIQUE (user_id)
);

-- ── WISHLIST_ITEMS ─────────────────────────────────────────────────
CREATE TABLE wishlist_items (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    wishlist_id  UUID          NOT NULL,
    book_id      UUID          NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    version      INTEGER       NOT NULL DEFAULT 0,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists (id),
    CONSTRAINT fk_wishlist_items_book     FOREIGN KEY (book_id)     REFERENCES books (id),
    CONSTRAINT uq_wishlist_items          UNIQUE (wishlist_id, book_id)
);

-- ── CARTS ──────────────────────────────────────────────────────────
CREATE TABLE carts (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    version     INTEGER       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_carts_user    FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_carts_status CHECK (status IN ('ACTIVE', 'CHECKED_OUT', 'ABANDONED'))
);

CREATE INDEX idx_carts_user_id ON carts (user_id);

-- ── CART_ITEMS ─────────────────────────────────────────────────────
CREATE TABLE cart_items (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id     UUID           NOT NULL,
    book_id     UUID           NOT NULL,
    quantity    INTEGER        NOT NULL,
    unit_price  NUMERIC(12,2)  NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    version     INTEGER        NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id),
    CONSTRAINT fk_cart_items_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT chk_cart_items_qty CHECK (quantity > 0),
    CONSTRAINT uq_cart_items      UNIQUE (cart_id, book_id)
);

-- ── ORDERS ─────────────────────────────────────────────────────────
CREATE TABLE orders (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID           NOT NULL,
    address_id        UUID           NOT NULL,
    coupon_id         UUID,
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    subtotal          NUMERIC(12,2)  NOT NULL,
    discount_amount   NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    shipping_cost     NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    total_amount      NUMERIC(12,2)  NOT NULL,
    gift_points_used  INTEGER        NOT NULL DEFAULT 0,
    placed_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    cancel_deadline   TIMESTAMPTZ,
    notes             TEXT,
    version           INTEGER        NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_orders_user    FOREIGN KEY (user_id)    REFERENCES users (id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses (id),
    CONSTRAINT fk_orders_coupon  FOREIGN KEY (coupon_id)  REFERENCES coupons (id),
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED','RETURNED'))
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status  ON orders (status);

-- ── ORDER_ITEMS ────────────────────────────────────────────────────
CREATE TABLE order_items (
    id                  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID           NOT NULL,
    book_id             UUID           NOT NULL,
    quantity            INTEGER        NOT NULL,
    unit_price          NUMERIC(12,2)  NOT NULL,
    estimated_delivery  DATE,
    created_at          TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by          VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by          VARCHAR(100)   NOT NULL DEFAULT 'system',
    version             INTEGER        NOT NULL DEFAULT 0,
    is_deleted          BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_items_book  FOREIGN KEY (book_id)  REFERENCES books (id),
    CONSTRAINT chk_order_items_qty  CHECK (quantity > 0)
);

-- ── PAYMENTS ───────────────────────────────────────────────────────
CREATE TABLE payments (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id          UUID           NOT NULL,
    method            VARCHAR(30)    NOT NULL,
    status            VARCHAR(20)    NOT NULL DEFAULT 'INITIATED',
    amount            NUMERIC(12,2)  NOT NULL,
    transaction_ref   VARCHAR(255),
    gateway_response  TEXT,
    paid_at           TIMESTAMPTZ,
    refunded_at       TIMESTAMPTZ,
    version           INTEGER        NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_payments_order   FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_payments_order   UNIQUE (order_id),
    CONSTRAINT chk_payments_method CHECK (method IN ('CREDIT_CARD','DEBIT_CARD','WALLET','GIFT_CARD','POINTS')),
    CONSTRAINT chk_payments_status CHECK (status IN ('INITIATED','SUCCESS','FAILED','REFUNDED'))
);

-- ── WALLETS ────────────────────────────────────────────────────────
CREATE TABLE wallets (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID           NOT NULL,
    balance     NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    version     INTEGER        NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_wallets_user UNIQUE (user_id),
    CONSTRAINT chk_wallets_bal CHECK (balance >= 0)
);

-- ── WALLET_TRANSACTIONS ────────────────────────────────────────────
CREATE TABLE wallet_transactions (
    id            UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id     UUID           NOT NULL,
    type          VARCHAR(10)    NOT NULL,
    amount        NUMERIC(12,2)  NOT NULL,
    reference_id  UUID,
    description   VARCHAR(255),
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by    VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by    VARCHAR(100)   NOT NULL DEFAULT 'system',
    version       INTEGER        NOT NULL DEFAULT 0,
    is_deleted    BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wallet_tx_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT chk_wallet_tx_type  CHECK (type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT chk_wallet_tx_amt   CHECK (amount > 0)
);

-- ── GIFT_POINTS ────────────────────────────────────────────────────
CREATE TABLE gift_points (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    points      INTEGER       NOT NULL DEFAULT 0,
    expires_at  TIMESTAMPTZ,
    version     INTEGER       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_gift_points_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_gift_points_pts CHECK (points >= 0)
);

-- ── SHIPMENTS ──────────────────────────────────────────────────────
CREATE TABLE shipments (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID          NOT NULL,
    carrier             VARCHAR(100),
    tracking_number     VARCHAR(100),
    status              VARCHAR(20)   NOT NULL DEFAULT 'CREATED',
    estimated_delivery  DATE,
    dispatched_at       TIMESTAMPTZ,
    delivered_at        TIMESTAMPTZ,
    version             INTEGER       NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by          VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by          VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted          BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_shipments_order   FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_shipments_order   UNIQUE (order_id),
    CONSTRAINT chk_shipments_status CHECK (status IN ('CREATED','DISPATCHED','IN_TRANSIT','DELIVERED','CANCELLED','RETURNED'))
);

-- ── SHIPPING_RATES ─────────────────────────────────────────────────
CREATE TABLE shipping_rates (
    id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    zone         VARCHAR(100)   NOT NULL,
    weight_from  NUMERIC(8,3)   NOT NULL,
    weight_to    NUMERIC(8,3)   NOT NULL,
    rate         NUMERIC(12,2)  NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)   NOT NULL DEFAULT 'system',
    version      INTEGER        NOT NULL DEFAULT 0,
    is_deleted   BOOLEAN        NOT NULL DEFAULT FALSE
);

-- ── REVIEWS ────────────────────────────────────────────────────────
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
    CONSTRAINT fk_reviews_user   FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reviews_book   FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT uq_reviews         UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reviews_book_id ON reviews (book_id);

-- ── RECOMMENDATIONS ────────────────────────────────────────────────
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
    version     INTEGER        NOT NULL DEFAULT 0,
    is_deleted  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_recommendations_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_recommendations_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT uq_recommendations      UNIQUE (user_id, book_id)
);

CREATE INDEX idx_recommendations_user_id ON recommendations (user_id);
