-- ============================================================
-- 003_orders.sql  —  Shopping: carts, cart_items, wishlists,
--                     wishlist_items, orders, order_items
-- ============================================================

-- Carts
CREATE TABLE carts (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    version     INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100) NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_carts_status CHECK (status IN ('ACTIVE', 'CHECKED_OUT', 'ABANDONED'))
);

CREATE INDEX idx_carts_user_id ON carts (user_id);

-- Cart Items
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
    is_deleted  BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id),
    CONSTRAINT fk_cart_items_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT chk_cart_items_qty CHECK (quantity > 0),
    CONSTRAINT uq_cart_items UNIQUE (cart_id, book_id)
);

-- Wishlists
CREATE TABLE wishlists (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_wishlists_user UNIQUE (user_id)
);

-- Wishlist Items
CREATE TABLE wishlist_items (
    id           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    wishlist_id  UUID          NOT NULL,
    book_id      UUID          NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by   VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists (id),
    CONSTRAINT fk_wishlist_items_book     FOREIGN KEY (book_id)     REFERENCES books (id),
    CONSTRAINT uq_wishlist_items UNIQUE (wishlist_id, book_id)
);

-- Coupons (needed before orders FK)
CREATE TABLE coupons (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    code              VARCHAR(50)    NOT NULL,
    description       TEXT,
    discount_type     VARCHAR(20)    NOT NULL,
    discount_value    NUMERIC(12,2)  NOT NULL,
    min_order_value   NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    max_uses          INTEGER,
    used_count        INTEGER        NOT NULL DEFAULT 0,
    expires_at        TIMESTAMPTZ,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,
    version           INTEGER        NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_by        VARCHAR(100)   NOT NULL DEFAULT 'system',
    is_deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_coupons_code       UNIQUE (code),
    CONSTRAINT chk_coupons_type      CHECK (discount_type IN ('PERCENTAGE', 'FIXED')),
    CONSTRAINT chk_coupons_value     CHECK (discount_value > 0)
);

-- Orders
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

-- Order Items
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
    is_deleted          BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_items_book  FOREIGN KEY (book_id)  REFERENCES books (id),
    CONSTRAINT chk_order_items_qty  CHECK (quantity > 0)
);
