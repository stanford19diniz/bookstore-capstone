-- ============================================================
-- 007_seed_catalog.sql  —  Seed: categories, publishers,
--                           authors, books for demo/testing
-- ============================================================

-- Seed Categories
INSERT INTO categories (name, slug, display_order, created_by, updated_by) VALUES
    ('Fiction',         'fiction',          1,  'system', 'system'),
    ('Non-Fiction',     'non-fiction',       2,  'system', 'system'),
    ('Science',         'science',          3,  'system', 'system'),
    ('History',         'history',          4,  'system', 'system'),
    ('Technology',      'technology',       5,  'system', 'system'),
    ('Self-Help',       'self-help',        6,  'system', 'system'),
    ('Biography',       'biography',        7,  'system', 'system'),
    ('Children',        'children',         8,  'system', 'system');

-- Seed Publishers
INSERT INTO publishers (name, country, created_by, updated_by) VALUES
    ('Penguin Random House', 'USA',    'system', 'system'),
    ('HarperCollins',        'USA',    'system', 'system'),
    ('Bloomsbury',           'UK',     'system', 'system'),
    ('Oxford University',    'UK',     'system', 'system'),
    ('Rupa Publications',    'India',  'system', 'system');

-- Seed Authors
INSERT INTO authors (first_name, last_name, created_by, updated_by) VALUES
    ('George',    'Orwell',       'system', 'system'),
    ('J.K.',      'Rowling',      'system', 'system'),
    ('Yuval',     'Harari',       'system', 'system'),
    ('Cal',       'Newport',      'system', 'system'),
    ('Robert',    'Martin',       'system', 'system');

-- Seed Books (5 sample books)
WITH
  pub_penguin AS (SELECT id FROM publishers WHERE name = 'Penguin Random House'),
  pub_bloom   AS (SELECT id FROM publishers WHERE name = 'Bloomsbury'),
  pub_harper  AS (SELECT id FROM publishers WHERE name = 'HarperCollins'),
  pub_oxford  AS (SELECT id FROM publishers WHERE name = 'Oxford University')
INSERT INTO books (title, isbn, publisher_id, description, price, stock_quantity, language, pages, published_date, delivery_days, created_by, updated_by)
VALUES
    ('1984',
     '9780451524935',
     (SELECT id FROM pub_penguin),
     'A dystopian novel set in a totalitarian society.',
     299.00, 100, 'English', 328, '1949-06-08', 3, 'system', 'system'),

    ('Harry Potter and the Philosopher''s Stone',
     '9780747532699',
     (SELECT id FROM pub_bloom),
     'The first novel in the Harry Potter series.',
     499.00, 200, 'English', 223, '1997-06-26', 3, 'system', 'system'),

    ('Sapiens: A Brief History of Humankind',
     '9780062316097',
     (SELECT id FROM pub_harper),
     'A brief history of humankind from the Stone Age.',
     599.00, 150, 'English', 443, '2011-01-01', 4, 'system', 'system'),

    ('Deep Work',
     '9781455586691',
     (SELECT id FROM pub_harper),
     'Rules for focused success in a distracted world.',
     449.00, 80, 'English', 304, '2016-01-05', 4, 'system', 'system'),

    ('Clean Code',
     '9780132350884',
     (SELECT id FROM pub_oxford),
     'A handbook of agile software craftsmanship.',
     799.00, 60, 'English', 431, '2008-08-01', 5, 'system', 'system');
