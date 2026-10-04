-- ============================================================
-- 004_payments.sql  —  Payment: payments, wallets,
--                       wallet_transactions, gift_points
-- ============================================================

-- Payments
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
    CONSTRAINT fk_payments_order  FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_payments_order  UNIQUE (order_id),
    CONSTRAINT chk_payments_method CHECK (method IN ('CREDIT_CARD','DEBIT_CARD','WALLET','GIFT_CARD','POINTS')),
    CONSTRAINT chk_payments_status CHECK (status IN ('INITIATED','SUCCESS','FAILED','REFUNDED'))
);

-- Wallets
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
    CONSTRAINT fk_wallets_user  FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_wallets_user  UNIQUE (user_id),
    CONSTRAINT chk_wallets_bal  CHECK (balance >= 0)
);

-- Wallet Transactions
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
    is_deleted    BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_wallet_tx_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT chk_wallet_tx_type  CHECK (type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT chk_wallet_tx_amt   CHECK (amount > 0)
);

-- Gift Points
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
