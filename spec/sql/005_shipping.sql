-- ============================================================
-- 005_shipping.sql  —  Shipping: shipments, shipping_rates
-- ============================================================

-- Shipments
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
    CONSTRAINT fk_shipments_order  FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_shipments_order  UNIQUE (order_id),
    CONSTRAINT chk_shipments_status CHECK (status IN ('CREATED','DISPATCHED','IN_TRANSIT','DELIVERED','CANCELLED','RETURNED'))
);

-- Shipping Rates
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
    is_deleted   BOOLEAN        NOT NULL DEFAULT FALSE
);

-- Seed standard shipping rates
INSERT INTO shipping_rates (zone, weight_from, weight_to, rate, created_by, updated_by) VALUES
    ('DOMESTIC',       0.000, 0.500, 29.99, 'system', 'system'),
    ('DOMESTIC',       0.501, 1.000, 49.99, 'system', 'system'),
    ('DOMESTIC',       1.001, 5.000, 79.99, 'system', 'system'),
    ('INTERNATIONAL',  0.000, 0.500, 199.99, 'system', 'system'),
    ('INTERNATIONAL',  0.501, 1.000, 299.99, 'system', 'system');
