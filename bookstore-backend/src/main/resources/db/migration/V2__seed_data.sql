-- =====================================================================
-- V2__seed_data.sql
-- Seed: roles, categories, publishers, authors, shipping rates, books
-- =====================================================================

-- ── ROLES ──────────────────────────────────────────────────────────
INSERT INTO roles (name, created_by, updated_by) VALUES
    ('ROLE_GUEST',    'system', 'system'),
    ('ROLE_CUSTOMER', 'system', 'system'),
    ('ROLE_ADMIN',    'system', 'system');

-- ── CATEGORIES ─────────────────────────────────────────────────────
INSERT INTO categories (name, slug, display_order, created_by, updated_by) VALUES
    ('Fiction',      'fiction',      1, 'system', 'system'),
    ('Non-Fiction',  'non-fiction',  2, 'system', 'system'),
    ('Science',      'science',      3, 'system', 'system'),
    ('History',      'history',      4, 'system', 'system'),
    ('Technology',   'technology',   5, 'system', 'system'),
    ('Self-Help',    'self-help',    6, 'system', 'system'),
    ('Biography',    'biography',    7, 'system', 'system'),
    ('Children',     'children',     8, 'system', 'system');

-- ── PUBLISHERS ─────────────────────────────────────────────────────
INSERT INTO publishers (name, country, created_by, updated_by) VALUES
    ('Penguin Random House', 'USA',   'system', 'system'),
    ('HarperCollins',        'USA',   'system', 'system'),
    ('Bloomsbury',           'UK',    'system', 'system'),
    ('Oxford University',    'UK',    'system', 'system'),
    ('Rupa Publications',    'India', 'system', 'system');

-- ── AUTHORS ────────────────────────────────────────────────────────
INSERT INTO authors (first_name, last_name, created_by, updated_by) VALUES
    ('George',  'Orwell',   'system', 'system'),
    ('J.K.',    'Rowling',  'system', 'system'),
    ('Yuval',   'Harari',   'system', 'system'),
    ('Cal',     'Newport',  'system', 'system'),
    ('Robert',  'Martin',   'system', 'system');

-- ── SHIPPING RATES ─────────────────────────────────────────────────
INSERT INTO shipping_rates (zone, weight_from, weight_to, rate, created_by, updated_by) VALUES
    ('DOMESTIC',      0.000, 0.500,  29.99,  'system', 'system'),
    ('DOMESTIC',      0.501, 1.000,  49.99,  'system', 'system'),
    ('DOMESTIC',      1.001, 5.000,  79.99,  'system', 'system'),
    ('INTERNATIONAL', 0.000, 0.500,  199.99, 'system', 'system'),
    ('INTERNATIONAL', 0.501, 1.000,  299.99, 'system', 'system');

-- ── BOOKS ──────────────────────────────────────────────────────────
INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity,
                   language, pages, published_date, delivery_days, created_by, updated_by)
SELECT '1984', '9780451524935',
       p.id, 'A dystopian novel set in a totalitarian society.',
       299.00, 100, 'English', 328, '1949-06-08', 3, 'system', 'system'
FROM publishers p WHERE p.name = 'Penguin Random House';

INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity,
                   language, pages, published_date, delivery_days, created_by, updated_by)
SELECT 'Harry Potter and the Philosopher''s Stone', '9780747532699',
       p.id, 'The first novel in the Harry Potter series.',
       499.00, 200, 'English', 223, '1997-06-26', 3, 'system', 'system'
FROM publishers p WHERE p.name = 'Bloomsbury';

INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity,
                   language, pages, published_date, delivery_days, created_by, updated_by)
SELECT 'Sapiens: A Brief History of Humankind', '9780062316097',
       p.id, 'A brief history of humankind from the Stone Age.',
       599.00, 150, 'English', 443, '2011-01-01', 4, 'system', 'system'
FROM publishers p WHERE p.name = 'HarperCollins';

INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity,
                   language, pages, published_date, delivery_days, created_by, updated_by)
SELECT 'Deep Work', '9781455586691',
       p.id, 'Rules for focused success in a distracted world.',
       449.00, 80, 'English', 304, '2016-01-05', 4, 'system', 'system'
FROM publishers p WHERE p.name = 'HarperCollins';

INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity,
                   language, pages, published_date, delivery_days, created_by, updated_by)
SELECT 'Clean Code', '9780132350884',
       p.id, 'A handbook of agile software craftsmanship.',
       799.00, 60, 'English', 431, '2008-08-01', 5, 'system', 'system'
FROM publishers p WHERE p.name = 'Oxford University';

-- ── BOOK_AUTHORS ───────────────────────────────────────────────────
INSERT INTO book_authors (book_id, author_id, created_by, updated_by)
SELECT b.id, a.id, 'system', 'system'
FROM books b, authors a
WHERE b.isbn = '9780451524935' AND a.last_name = 'Orwell';

INSERT INTO book_authors (book_id, author_id, created_by, updated_by)
SELECT b.id, a.id, 'system', 'system'
FROM books b, authors a
WHERE b.isbn = '9780747532699' AND a.last_name = 'Rowling';

INSERT INTO book_authors (book_id, author_id, created_by, updated_by)
SELECT b.id, a.id, 'system', 'system'
FROM books b, authors a
WHERE b.isbn = '9780062316097' AND a.last_name = 'Harari';

INSERT INTO book_authors (book_id, author_id, created_by, updated_by)
SELECT b.id, a.id, 'system', 'system'
FROM books b, authors a
WHERE b.isbn = '9781455586691' AND a.last_name = 'Newport';

INSERT INTO book_authors (book_id, author_id, created_by, updated_by)
SELECT b.id, a.id, 'system', 'system'
FROM books b, authors a
WHERE b.isbn = '9780132350884' AND a.last_name = 'Martin';

-- ── BOOK_CATEGORIES ────────────────────────────────────────────────
INSERT INTO book_categories (book_id, category_id, created_by, updated_by)
SELECT b.id, c.id, 'system', 'system'
FROM books b, categories c
WHERE b.isbn = '9780451524935' AND c.slug = 'fiction';

INSERT INTO book_categories (book_id, category_id, created_by, updated_by)
SELECT b.id, c.id, 'system', 'system'
FROM books b, categories c
WHERE b.isbn = '9780747532699' AND c.slug = 'fiction';

INSERT INTO book_categories (book_id, category_id, created_by, updated_by)
SELECT b.id, c.id, 'system', 'system'
FROM books b, categories c
WHERE b.isbn = '9780062316097' AND c.slug = 'history';

INSERT INTO book_categories (book_id, category_id, created_by, updated_by)
SELECT b.id, c.id, 'system', 'system'
FROM books b, categories c
WHERE b.isbn = '9781455586691' AND c.slug = 'self-help';

INSERT INTO book_categories (book_id, category_id, created_by, updated_by)
SELECT b.id, c.id, 'system', 'system'
FROM books b, categories c
WHERE b.isbn = '9780132350884' AND c.slug = 'technology';
