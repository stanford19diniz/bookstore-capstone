# eCommerce Bookstore — Backend Requirements
> Generated via Prompt 1 | Source: Wireframes (Slides 3–10) + Architecture Diagram (Slide 4)

---

## 1. Functional Requirements

### FR-01 Authentication & Member Management
- FR-01.1 Guest users can browse the catalog without logging in
- FR-01.2 Registered users can sign up with email and password
- FR-01.3 Registered users can log in and receive a JWT token
- FR-01.4 Users can log out (token invalidation)
- FR-01.5 Role-based access: GUEST, CUSTOMER, ADMIN
- FR-01.6 Admin can manage users, roles and entitlements

### FR-02 Store Management
- FR-02.1 Admin can create and manage stores
- FR-02.2 Admin can create and manage catalogs per store
- FR-02.3 Admin can define store policies (return, shipping, cancellation)

### FR-03 Catalog — Books, Categories, Authors, Publishers
- FR-03.1 Admin can create/update/delete books
- FR-03.2 Each book has: title, ISBN, author(s), publisher, category, price, stock, description, cover image, tentative delivery date
- FR-03.3 Books are organised into categories (e.g. Fiction, Non-Fiction, Science, History)
- FR-03.4 Books are associated with one or more authors and a publisher
- FR-03.5 Customers can browse catalog filtered by category and/or brand/publisher
- FR-03.6 Catalog is browsable by entitlement (e.g. premium-only titles)
- FR-03.7 Related/recommended books appear on the product detail page
- FR-03.8 Upsell and cross-sell products are surfaced based on order history

### FR-04 Search
- FR-04.1 Customers can search books by title, author, publisher, ISBN, category
- FR-04.2 Search results support pagination, sorting (price, relevance, date), and filtering

### FR-05 Wishlist
- FR-05.1 Authenticated customers can add/remove books to a personal wishlist
- FR-05.2 Wishlist is persisted across sessions

### FR-06 Shopping Cart
- FR-06.1 Customers can add books to the cart
- FR-06.2 Customers can update quantities or remove items from the cart
- FR-06.3 Cart persists for authenticated users; session-based for guests
- FR-06.4 Recommended items are shown in the cart based on order history

### FR-07 Order Management
- FR-07.1 Customers can place an order from the cart (checkout)
- FR-07.2 Customers must provide a delivery address at checkout
- FR-07.3 Orders can be confirmed, cancelled (within 48 hours), or returned
- FR-07.4 Customers can view full order history
- FR-07.5 "Buy Again" feature available from order history
- FR-07.6 Each order item shows the tentative delivery date

### FR-08 Coupons & Gift Points
- FR-08.1 Customers can apply coupon codes at checkout
- FR-08.2 Customers can redeem accumulated gift points at checkout
- FR-08.3 Admin can create, activate, and deactivate coupons

### FR-09 Payment
- FR-09.1 Customers can pay via: Credit card, Debit card, Gift card, Wallet
- FR-09.2 Payment is processed via an external payment gateway (abstracted)
- FR-09.3 Payment confirmation is sent after successful processing
- FR-09.4 Refunds are supported for cancelled/returned orders
- FR-09.5 Wallet balance can be topped up and used for payment

### FR-10 Shipping
- FR-10.1 Shipping rate is calculated at checkout based on delivery address and weight
- FR-10.2 Approximate delivery date is displayed to the customer
- FR-10.3 Return shipment is supported for eligible orders

### FR-11 Reviews
- FR-11.1 Authenticated customers who have purchased a book can leave a review (rating + comment)
- FR-11.2 Reviews are visible on the book detail page
- FR-11.3 Admin can moderate (approve/delete) reviews

### FR-12 Recommendations
- FR-12.1 Recommendations are generated based on a customer's order history
- FR-12.2 Recommendations surface on the home page, catalog, cart, and product pages

---

## 2. Non-Functional Requirements

| ID     | Category        | Requirement                                                                 |
|--------|-----------------|-----------------------------------------------------------------------------|
| NFR-01 | Performance     | API response time ≤ 500ms for 95th percentile under normal load             |
| NFR-02 | Scalability     | Stateless services; horizontally scalable                                   |
| NFR-03 | Security        | JWT-based authentication; HTTPS only; no secrets in code                    |
| NFR-04 | Availability    | 99.9% uptime target for production deployment                               |
| NFR-05 | Maintainability | OpenAPI spec as single source of truth; layered architecture                |
| NFR-06 | Testability     | Minimum 80% unit test coverage; integration tests via Testcontainers        |
| NFR-07 | Observability   | Structured logging; health check endpoints; optional distributed tracing    |
| NFR-08 | Portability     | Runs locally on PostgreSQL 17 and deployable to Supabase / AWS RDS          |
| NFR-09 | Data Integrity  | Optimistic locking; soft deletes; audit columns on all tables               |
| NFR-10 | API Standards   | REST Level 2+; Problem Details (RFC 7807) error responses; versioned URIs   |

---

## 3. User Roles

| Role     | Description                                                         |
|----------|---------------------------------------------------------------------|
| GUEST    | Unauthenticated user; can browse catalog and search                 |
| CUSTOMER | Authenticated registered user; can shop, order, review             |
| ADMIN    | Platform administrator; manages catalog, users, orders, policies   |

---

## 4. User Stories

### Member
- As a **guest**, I want to browse the catalog so that I can discover books without registering
- As a **guest**, I want to register with my email and password so that I can become a customer
- As a **customer**, I want to log in so that I can access my account, cart, and orders
- As a **customer**, I want to log out so that my session is secure

### Catalog
- As a **customer**, I want to filter books by category and publisher so that I find what I'm looking for faster
- As a **customer**, I want to see related books on a product page so that I discover relevant titles
- As a **customer**, I want to see recommended books based on my history so that I get personalised suggestions

### Cart & Wishlist
- As a **customer**, I want to add books to my wishlist so that I can save them for later
- As a **customer**, I want to add books to my cart so that I can purchase them
- As a **customer**, I want to update quantities in my cart so that I control my purchase

### Orders
- As a **customer**, I want to checkout my cart so that I can place an order
- As a **customer**, I want to provide a delivery address so that my order reaches me
- As a **customer**, I want to cancel an order within 48 hours so that I can change my mind
- As a **customer**, I want to view my order history so that I can track past purchases
- As a **customer**, I want to "Buy Again" from order history so that I can reorder easily

### Payments
- As a **customer**, I want to pay by credit/debit card so that I complete my purchase securely
- As a **customer**, I want to redeem gift points at checkout so that I save money
- As a **customer**, I want to apply a coupon code so that I get a discount
- As a **customer**, I want to receive payment confirmation so that I know my order is placed

### Admin
- As an **admin**, I want to manage the book catalog so that products are up to date
- As an **admin**, I want to create and manage coupons so that I run promotions
- As an **admin**, I want to moderate reviews so that quality is maintained

---

## 5. Business Capabilities

| Capability              | Description                                              |
|-------------------------|----------------------------------------------------------|
| Identity & Access       | Authentication, authorisation, role management           |
| Catalog Management      | Books, categories, authors, publishers                   |
| Discovery & Search      | Full-text search, filtering, recommendations             |
| Shopping                | Wishlist, cart management                                |
| Order Fulfilment        | Checkout, order lifecycle, cancellation, returns         |
| Promotions              | Coupons, gift points, wallet                             |
| Payment Processing      | Multi-method payment, refunds, confirmation              |
| Logistics               | Shipping rate, delivery estimate, return shipment        |
| Customer Engagement     | Reviews, ratings, personalised recommendations           |
| Platform Administration | Store, catalog, user, policy management                  |

---

## 6. Domain Boundaries

| Domain     | Owns                                              | Does NOT own                    |
|------------|---------------------------------------------------|---------------------------------|
| Member     | User, Role, Entitlement, Session                  | Order, Payment                  |
| Store      | Store, StorePolicy, StoreCatalog                  | Books (belongs to Catalog)      |
| Catalog    | Book, Category, Author, Publisher, BookReview     | Cart, Order                     |
| Order      | Cart, CartItem, Order, OrderItem, Address         | Payment details, Shipping rate  |
| Payment    | Payment, PaymentMethod, Wallet, GiftCard, Refund  | Order status, Shipping          |
| Shipping   | Shipment, ShippingRate, DeliveryEstimate          | Order, Payment                  |
| Promotion  | Coupon, GiftPoint, Discount                       | Payment processing              |

---

## 7. Backend Modules

1. `auth`           — JWT generation, validation, refresh
2. `member`         — User registration, profile, roles
3. `store`          — Store and policy management
4. `catalog`        — Books, categories, authors, publishers
5. `search`         — Full-text search, filters, sorting
6. `wishlist`       — Wishlist CRUD per user
7. `cart`           — Cart and cart item management
8. `order`          — Checkout, order lifecycle, history
9. `promotion`      — Coupons, gift points
10. `payment`       — Payment processing, refunds, wallet
11. `shipping`      — Rates, delivery estimates, returns
12. `review`        — Book reviews and ratings
13. `recommendation`— Personalised book recommendations

---

## 8. API Domains

| API Domain      | Base URI                    |
|-----------------|-----------------------------|
| Authentication  | `/api/v1/auth`              |
| Users           | `/api/v1/users`             |
| Books           | `/api/v1/books`             |
| Categories      | `/api/v1/categories`        |
| Authors         | `/api/v1/authors`           |
| Publishers      | `/api/v1/publishers`        |
| Search          | `/api/v1/search`            |
| Wishlist        | `/api/v1/wishlist`          |
| Cart            | `/api/v1/cart`              |
| Orders          | `/api/v1/orders`            |
| Payments        | `/api/v1/payments`          |
| Shipping        | `/api/v1/shipments`         |
| Reviews         | `/api/v1/reviews`           |
| Coupons         | `/api/v1/coupons`           |
| Recommendations | `/api/v1/recommendations`   |

---

## 9. Data Ownership Matrix

| Entity          | Owned By    | Referenced By               |
|-----------------|-------------|-----------------------------|
| User            | Member      | Order, Review, Cart         |
| Role            | Member      | User                        |
| Store           | Store       | Catalog                     |
| Book            | Catalog     | CartItem, OrderItem, Review |
| Category        | Catalog     | Book                        |
| Author          | Catalog     | Book                        |
| Publisher       | Catalog     | Book                        |
| Cart            | Order       | User, Book                  |
| Order           | Order       | User, Book, Address         |
| Address         | Member      | Order, Shipment             |
| Payment         | Payment     | Order                       |
| Wallet          | Payment     | User                        |
| Coupon          | Promotion   | Order                       |
| GiftPoint       | Promotion   | User                        |
| Shipment        | Shipping    | Order                       |
| Review          | Catalog     | User, Book                  |

---

## 10. Assumptions

- A single PostgreSQL 17 database (`bookstore`) is used locally; Supabase for cloud deployment
- Payment gateway is abstracted — no real third-party integration required for capstone
- Recommendation engine is rule-based (order history); no ML model required
- Email notifications are out of scope for initial delivery
- Image storage uses URLs (no binary blob storage)
- A single store/tenant is assumed for this capstone
- Guest cart is session-based and not persisted in the database

---

## 11. Out of Scope

- Real payment gateway integration (Stripe, PayPal, etc.)
- Email / SMS notification service
- ML-based recommendation engine
- Multi-tenant / multi-store support
- Admin dashboard UI (frontend)
- Mobile application
- Inventory management / warehouse system
- Tax calculation engine
