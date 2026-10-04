# eCommerce Bookstore — API Resource Model
> Generated via Prompt 5 | Source: spec/requirements.md + spec/domain-model.md + spec/data-model.md

---

## Design Principles
- Base path: `/api/v1`
- All endpoints return: `{ "data": ..., "meta": {...}, "errors": [] }`
- Errors follow RFC 7807 Problem Details
- Pagination params: `page` (0-based), `size` (default 20), `sort` (field,asc|desc)
- JWT Bearer token required for all non-public endpoints
- Soft delete: DELETE endpoints set `is_deleted=true`, never physical delete

---

## 1. Authentication  `/api/v1/auth`

| # | Resource        | Method | URI                        | Request DTO            | Response DTO          | Auth    | Description                  |
|---|-----------------|--------|----------------------------|------------------------|-----------------------|---------|------------------------------|
| 1 | Register        | POST   | `/auth/register`           | RegisterRequest        | UserResponse          | PUBLIC  | Create new customer account  |
| 2 | Login           | POST   | `/auth/login`              | LoginRequest           | AuthResponse          | PUBLIC  | Authenticate, return JWT     |
| 3 | Refresh Token   | POST   | `/auth/refresh`            | RefreshTokenRequest    | AuthResponse          | PUBLIC  | Refresh expired JWT          |
| 4 | Logout          | POST   | `/auth/logout`             | —                      | MessageResponse       | CUSTOMER| Invalidate token             |
| 5 | Me              | GET    | `/auth/me`                 | —                      | UserResponse          | CUSTOMER| Get current user profile     |

### DTOs
**RegisterRequest:** `firstName`, `lastName`, `email`, `password` (min 8 chars), `phone?`
**LoginRequest:** `email`, `password`
**AuthResponse:** `accessToken`, `refreshToken`, `tokenType`, `expiresIn`, `user` (UserResponse)
**RefreshTokenRequest:** `refreshToken`

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| AUTH_001 | 400 | Validation failure |
| AUTH_002 | 409 | Email already registered |
| AUTH_003 | 401 | Invalid credentials |
| AUTH_004 | 401 | Token expired or invalid |

---

## 2. Users  `/api/v1/users`

| # | Resource         | Method | URI                        | Request DTO            | Response DTO          | Auth    | Description              |
|---|------------------|--------|----------------------------|------------------------|-----------------------|---------|--------------------------|
| 1 | Get Profile      | GET    | `/users/{id}`              | —                      | UserResponse          | CUSTOMER| Get user by ID           |
| 2 | Update Profile   | PUT    | `/users/{id}`              | UpdateUserRequest      | UserResponse          | CUSTOMER| Update own profile       |
| 3 | List Users       | GET    | `/users`                   | page, size, sort       | Page<UserResponse>    | ADMIN   | List all users           |
| 4 | Deactivate User  | DELETE | `/users/{id}`              | —                      | MessageResponse       | ADMIN   | Soft-delete user         |
| 5 | List Addresses   | GET    | `/users/{id}/addresses`    | —                      | List<AddressResponse> | CUSTOMER| Get user addresses       |
| 6 | Add Address      | POST   | `/users/{id}/addresses`    | AddressRequest         | AddressResponse       | CUSTOMER| Add new address          |
| 7 | Update Address   | PUT    | `/users/{id}/addresses/{addressId}` | AddressRequest | AddressResponse  | CUSTOMER| Update address           |
| 8 | Delete Address   | DELETE | `/users/{id}/addresses/{addressId}` | —          | MessageResponse       | CUSTOMER| Remove address           |

### DTOs
**UserResponse:** `id`, `email`, `firstName`, `lastName`, `phone`, `isActive`, `roles`, `createdAt`
**UpdateUserRequest:** `firstName`, `lastName`, `phone`
**AddressRequest:** `label?`, `line1`, `line2?`, `city`, `state`, `country`, `pincode`, `isDefault`
**AddressResponse:** `id`, `label`, `line1`, `line2`, `city`, `state`, `country`, `pincode`, `isDefault`

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| USER_001 | 404 | User not found |
| USER_002 | 403 | Access denied (not own profile) |
| USER_003 | 404 | Address not found |

---

## 3. Categories  `/api/v1/categories`

| # | Resource         | Method | URI                          | Request DTO         | Response DTO             | Auth   | Description              |
|---|------------------|--------|------------------------------|---------------------|--------------------------|--------|--------------------------|
| 1 | List Categories  | GET    | `/categories`                | —                   | List<CategoryResponse>   | PUBLIC | Full category tree       |
| 2 | Get Category     | GET    | `/categories/{id}`           | —                   | CategoryResponse         | PUBLIC | Single category          |
| 3 | Create Category  | POST   | `/categories`                | CategoryRequest     | CategoryResponse         | ADMIN  | Create category          |
| 4 | Update Category  | PUT    | `/categories/{id}`           | CategoryRequest     | CategoryResponse         | ADMIN  | Update category          |
| 5 | Delete Category  | DELETE | `/categories/{id}`           | —                   | MessageResponse          | ADMIN  | Soft-delete category     |

### DTOs
**CategoryRequest:** `name`, `slug`, `parentId?`, `displayOrder`
**CategoryResponse:** `id`, `name`, `slug`, `parentId`, `displayOrder`, `children?`

---

## 4. Authors  `/api/v1/authors`

| # | Resource      | Method | URI                   | Request DTO     | Response DTO          | Auth   | Description      |
|---|---------------|--------|-----------------------|-----------------|-----------------------|--------|------------------|
| 1 | List Authors  | GET    | `/authors`            | page, size, sort| Page<AuthorResponse>  | PUBLIC | Paginated list   |
| 2 | Get Author    | GET    | `/authors/{id}`       | —               | AuthorResponse        | PUBLIC | Author detail    |
| 3 | Create Author | POST   | `/authors`            | AuthorRequest   | AuthorResponse        | ADMIN  | Create author    |
| 4 | Update Author | PUT    | `/authors/{id}`       | AuthorRequest   | AuthorResponse        | ADMIN  | Update author    |
| 5 | Delete Author | DELETE | `/authors/{id}`       | —               | MessageResponse       | ADMIN  | Soft-delete      |

### DTOs
**AuthorRequest:** `firstName`, `lastName`, `bio?`, `profileImageUrl?`
**AuthorResponse:** `id`, `firstName`, `lastName`, `bio`, `profileImageUrl`

---

## 5. Publishers  `/api/v1/publishers`

| # | Resource         | Method | URI                     | Request DTO        | Response DTO             | Auth   | Description      |
|---|------------------|--------|-------------------------|--------------------|--------------------------|--------|------------------|
| 1 | List Publishers  | GET    | `/publishers`           | page, size, sort   | Page<PublisherResponse>  | PUBLIC | Paginated list   |
| 2 | Get Publisher    | GET    | `/publishers/{id}`      | —                  | PublisherResponse        | PUBLIC | Publisher detail |
| 3 | Create Publisher | POST   | `/publishers`           | PublisherRequest   | PublisherResponse        | ADMIN  | Create           |
| 4 | Update Publisher | PUT    | `/publishers/{id}`      | PublisherRequest   | PublisherResponse        | ADMIN  | Update           |
| 5 | Delete Publisher | DELETE | `/publishers/{id}`      | —                  | MessageResponse          | ADMIN  | Soft-delete      |

### DTOs
**PublisherRequest:** `name`, `country?`, `websiteUrl?`
**PublisherResponse:** `id`, `name`, `country`, `websiteUrl`

---

## 6. Books  `/api/v1/books`

| # | Resource           | Method | URI                            | Request DTO       | Response DTO          | Auth     | Description                      |
|---|--------------------|--------|--------------------------------|-------------------|-----------------------|----------|----------------------------------|
| 1 | List Books         | GET    | `/books`                       | page, size, sort, categoryId?, publisherId?, minPrice?, maxPrice?, language? | Page<BookSummaryResponse> | PUBLIC | Filtered catalog |
| 2 | Get Book           | GET    | `/books/{id}`                  | —                 | BookDetailResponse    | PUBLIC   | Full book detail + reviews       |
| 3 | Create Book        | POST   | `/books`                       | BookRequest       | BookDetailResponse    | ADMIN    | Add book to catalog              |
| 4 | Update Book        | PUT    | `/books/{id}`                  | BookRequest       | BookDetailResponse    | ADMIN    | Update book                      |
| 5 | Delete Book        | DELETE | `/books/{id}`                  | —                 | MessageResponse       | ADMIN    | Soft-delete book                 |
| 6 | Get Related Books  | GET    | `/books/{id}/related`          | —                 | List<BookSummaryResponse> | PUBLIC| Related books for detail page   |

### DTOs
**BookRequest:** `title`, `isbn`, `publisherId`, `description?`, `price`, `stockQuantity`, `coverImageUrl?`, `language`, `pages?`, `publishedDate?`, `deliveryDays`, `authorIds[]`, `categoryIds[]`
**BookSummaryResponse:** `id`, `title`, `isbn`, `price`, `coverImageUrl`, `averageRating`, `reviewCount`, `deliveryDays`, `authors[]`, `categories[]`
**BookDetailResponse:** BookSummaryResponse + `description`, `publisher`, `language`, `pages`, `publishedDate`, `stockQuantity`, `isActive`

### Validation Rules
- `isbn`: must match ISBN-13 pattern `^97[89]\d{10}$`
- `price`: must be ≥ 0
- `stockQuantity`: must be ≥ 0
- `deliveryDays`: must be ≥ 1

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| BOOK_001 | 404 | Book not found |
| BOOK_002 | 409 | ISBN already exists |
| BOOK_003 | 400 | Invalid ISBN format |

---

## 7. Search  `/api/v1/search`

| # | Resource    | Method | URI            | Request DTO                              | Response DTO             | Auth   | Description              |
|---|-------------|--------|----------------|------------------------------------------|--------------------------|--------|--------------------------|
| 1 | Search Books| GET    | `/search/books`| `q` (required), `categoryId?`, `authorId?`, `publisherId?`, `minPrice?`, `maxPrice?`, `page`, `size`, `sort` | Page<BookSummaryResponse> | PUBLIC | Full-text search |

### Validation Rules
- `q`: min 2 characters, max 200 characters

---

## 8. Wishlist  `/api/v1/wishlist`

| # | Resource              | Method | URI                          | Request DTO        | Response DTO           | Auth     | Description             |
|---|-----------------------|--------|------------------------------|--------------------|------------------------|----------|-------------------------|
| 1 | Get My Wishlist       | GET    | `/wishlist`                  | —                  | WishlistResponse       | CUSTOMER | Get own wishlist        |
| 2 | Add to Wishlist       | POST   | `/wishlist/items`            | WishlistItemRequest| WishlistResponse       | CUSTOMER | Add book to wishlist    |
| 3 | Remove from Wishlist  | DELETE | `/wishlist/items/{bookId}`   | —                  | MessageResponse        | CUSTOMER | Remove book             |

### DTOs
**WishlistItemRequest:** `bookId`
**WishlistResponse:** `id`, `items[]` (each: `bookId`, `book` (BookSummaryResponse), `addedAt`)

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| WISH_001 | 404 | Book not found |
| WISH_002 | 409 | Book already in wishlist |

---

## 9. Cart  `/api/v1/cart`

| # | Resource          | Method | URI                      | Request DTO       | Response DTO    | Auth     | Description              |
|---|-------------------|--------|--------------------------|-------------------|-----------------|----------|--------------------------|
| 1 | Get My Cart       | GET    | `/cart`                  | —                 | CartResponse    | CUSTOMER | View cart with totals    |
| 2 | Add Item          | POST   | `/cart/items`            | CartItemRequest   | CartResponse    | CUSTOMER | Add book to cart         |
| 3 | Update Item Qty   | PATCH  | `/cart/items/{bookId}`   | UpdateQtyRequest  | CartResponse    | CUSTOMER | Update quantity          |
| 4 | Remove Item       | DELETE | `/cart/items/{bookId}`   | —                 | CartResponse    | CUSTOMER | Remove book from cart    |
| 5 | Clear Cart        | DELETE | `/cart`                  | —                 | MessageResponse | CUSTOMER | Empty entire cart        |

### DTOs
**CartItemRequest:** `bookId`, `quantity` (min 1)
**UpdateQtyRequest:** `quantity` (min 1)
**CartResponse:** `id`, `status`, `items[]`, `subtotal`, `itemCount`, `recommendations[]`
  - each item: `bookId`, `book` (BookSummaryResponse), `quantity`, `unitPrice`, `lineTotal`

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| CART_001 | 404 | Book not found |
| CART_002 | 422 | Insufficient stock |
| CART_003 | 404 | Cart item not found |

---

## 10. Orders  `/api/v1/orders`

| # | Resource          | Method | URI                          | Request DTO       | Response DTO           | Auth     | Description                      |
|---|-------------------|--------|------------------------------|-------------------|------------------------|----------|----------------------------------|
| 1 | Place Order       | POST   | `/orders`                    | PlaceOrderRequest | OrderResponse          | CUSTOMER | Checkout cart → create order     |
| 2 | Get Order         | GET    | `/orders/{id}`               | —                 | OrderResponse          | CUSTOMER | Order detail with items          |
| 3 | My Order History  | GET    | `/orders`                    | page, size, sort, status? | Page<OrderSummaryResponse> | CUSTOMER | Paginated order history  |
| 4 | Cancel Order      | POST   | `/orders/{id}/cancel`        | —                 | OrderResponse          | CUSTOMER | Cancel (≤48hr deadline)          |
| 5 | Return Order      | POST   | `/orders/{id}/return`        | ReturnRequest     | OrderResponse          | CUSTOMER | Initiate return                  |
| 6 | Buy Again         | POST   | `/orders/{id}/buy-again`     | —                 | CartResponse           | CUSTOMER | Re-add order items to cart       |
| 7 | List All Orders   | GET    | `/orders/admin`              | page, size, sort, status?, userId? | Page<OrderSummaryResponse> | ADMIN | Admin view all orders |

### DTOs
**PlaceOrderRequest:** `addressId`, `couponCode?`, `giftPointsToRedeem?`, `paymentMethod` (CREDIT_CARD/DEBIT_CARD/WALLET/GIFT_CARD)
**ReturnRequest:** `reason`
**OrderSummaryResponse:** `id`, `status`, `totalAmount`, `itemCount`, `placedAt`, `cancelDeadline`
**OrderResponse:** `id`, `status`, `items[]`, `address`, `subtotal`, `discountAmount`, `shippingCost`, `totalAmount`, `giftPointsUsed`, `placedAt`, `cancelDeadline`, `payment` (PaymentSummary), `shipment` (ShipmentSummary)
  - each item: `bookId`, `title`, `coverImageUrl`, `quantity`, `unitPrice`, `lineTotal`, `estimatedDelivery`

### Validation Rules
- `addressId`: must belong to the authenticated user
- `giftPointsToRedeem`: cannot exceed user's available points
- Cancel: `placed_at + 48 hours > now()`

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| ORD_001 | 422 | Cart is empty |
| ORD_002 | 422 | Insufficient stock at checkout |
| ORD_003 | 404 | Order not found |
| ORD_004 | 422 | Cancel deadline passed |
| ORD_005 | 400 | Invalid coupon code |
| ORD_006 | 422 | Insufficient gift points |

---

## 11. Payments  `/api/v1/payments`

| # | Resource          | Method | URI                          | Request DTO          | Response DTO      | Auth     | Description              |
|---|-------------------|--------|------------------------------|----------------------|-------------------|----------|--------------------------|
| 1 | Get Payment       | GET    | `/payments/{orderId}`        | —                    | PaymentResponse   | CUSTOMER | Payment for an order     |
| 2 | Process Payment   | POST   | `/payments`                  | PaymentRequest       | PaymentResponse   | CUSTOMER | Initiate payment         |
| 3 | Refund Payment    | POST   | `/payments/{id}/refund`      | —                    | PaymentResponse   | ADMIN    | Initiate refund          |
| 4 | Get Wallet        | GET    | `/payments/wallet`           | —                    | WalletResponse    | CUSTOMER | Current wallet balance   |
| 5 | Top Up Wallet     | POST   | `/payments/wallet/topup`     | TopUpRequest         | WalletResponse    | CUSTOMER | Add funds to wallet      |

### DTOs
**PaymentRequest:** `orderId`, `method` (CREDIT_CARD/DEBIT_CARD/WALLET/GIFT_CARD/POINTS), `amount`
**PaymentResponse:** `id`, `orderId`, `method`, `status`, `amount`, `transactionRef`, `paidAt`, `refundedAt`
**WalletResponse:** `id`, `balance`, `transactions[]` (recent 10)
**TopUpRequest:** `amount` (min 1.00)

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| PAY_001 | 404 | Order not found |
| PAY_002 | 422 | Insufficient wallet balance |
| PAY_003 | 422 | Payment already processed |
| PAY_004 | 502 | Gateway error |

---

## 12. Shipping  `/api/v1/shipments`

| # | Resource           | Method | URI                          | Request DTO | Response DTO       | Auth     | Description               |
|---|--------------------|--------|------------------------------|-------------|-------------------|----------|---------------------------|
| 1 | Get Shipment       | GET    | `/shipments/{orderId}`       | —           | ShipmentResponse  | CUSTOMER | Shipment for an order     |
| 2 | Calculate Rate     | POST   | `/shipments/calculate-rate`  | RateRequest | RateResponse      | PUBLIC   | Estimate shipping cost    |
| 3 | Update Shipment    | PATCH  | `/shipments/{id}`            | ShipmentUpdateRequest | ShipmentResponse | ADMIN | Update status / tracking |

### DTOs
**RateRequest:** `pincode`, `weightKg`
**RateResponse:** `zone`, `rate`, `estimatedDeliveryDays`
**ShipmentResponse:** `id`, `orderId`, `carrier`, `trackingNumber`, `status`, `estimatedDelivery`, `dispatchedAt`, `deliveredAt`
**ShipmentUpdateRequest:** `status`, `carrier?`, `trackingNumber?`, `estimatedDelivery?`

---

## 13. Reviews  `/api/v1/reviews`

| # | Resource          | Method | URI                          | Request DTO    | Response DTO          | Auth     | Description                   |
|---|-------------------|--------|------------------------------|----------------|-----------------------|----------|-------------------------------|
| 1 | List Book Reviews | GET    | `/reviews/books/{bookId}`    | page, size     | Page<ReviewResponse>  | PUBLIC   | Approved reviews for a book   |
| 2 | Submit Review     | POST   | `/reviews`                   | ReviewRequest  | ReviewResponse        | CUSTOMER | Submit a review               |
| 3 | My Reviews        | GET    | `/reviews/me`                | page, size     | Page<ReviewResponse>  | CUSTOMER | Own reviews                   |
| 4 | Approve Review    | PATCH  | `/reviews/{id}/approve`      | —              | ReviewResponse        | ADMIN    | Approve pending review        |
| 5 | Reject Review     | PATCH  | `/reviews/{id}/reject`       | —              | ReviewResponse        | ADMIN    | Reject pending review         |
| 6 | Delete Review     | DELETE | `/reviews/{id}`              | —              | MessageResponse       | ADMIN    | Soft-delete review            |

### DTOs
**ReviewRequest:** `bookId`, `rating` (1–5), `comment?`
**ReviewResponse:** `id`, `bookId`, `userId`, `userFirstName`, `rating`, `comment`, `status`, `createdAt`

### Validation Rules
- User must have purchased the book (order in DELIVERED status)
- One review per user per book

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| REV_001 | 403 | User has not purchased this book |
| REV_002 | 409 | Review already submitted |
| REV_003 | 404 | Review not found |

---

## 14. Coupons  `/api/v1/coupons`

| # | Resource         | Method | URI                     | Request DTO    | Response DTO         | Auth     | Description              |
|---|------------------|--------|-------------------------|----------------|----------------------|----------|--------------------------|
| 1 | Validate Coupon  | GET    | `/coupons/validate`     | `code`, `orderTotal` | CouponResponse | PUBLIC   | Check if coupon is valid |
| 2 | List Coupons     | GET    | `/coupons`              | page, size     | Page<CouponResponse> | ADMIN    | All coupons              |
| 3 | Create Coupon    | POST   | `/coupons`              | CouponRequest  | CouponResponse       | ADMIN    | Create coupon            |
| 4 | Update Coupon    | PUT    | `/coupons/{id}`         | CouponRequest  | CouponResponse       | ADMIN    | Update coupon            |
| 5 | Deactivate Coupon| PATCH  | `/coupons/{id}/deactivate` | —           | CouponResponse       | ADMIN    | Deactivate coupon        |

### DTOs
**CouponRequest:** `code`, `description?`, `discountType` (PERCENTAGE/FIXED), `discountValue`, `minOrderValue`, `maxUses?`, `expiresAt?`
**CouponResponse:** `id`, `code`, `discountType`, `discountValue`, `minOrderValue`, `maxUses`, `usedCount`, `expiresAt`, `isActive`

### Error Codes
| Code | HTTP | Scenario |
|------|------|----------|
| CPN_001 | 404 | Coupon not found |
| CPN_002 | 422 | Coupon expired |
| CPN_003 | 422 | Coupon usage limit reached |
| CPN_004 | 422 | Order total below minimum |
| CPN_005 | 409 | Coupon code already exists |

---

## 15. Recommendations  `/api/v1/recommendations`

| # | Resource             | Method | URI                       | Request DTO | Response DTO                | Auth     | Description                         |
|---|----------------------|--------|---------------------------|-------------|----------------------------|----------|-------------------------------------|
| 1 | My Recommendations   | GET    | `/recommendations`        | size?       | List<BookSummaryResponse>  | CUSTOMER | Personalised list based on history  |
| 2 | Homepage Featured    | GET    | `/recommendations/featured`| size?      | List<BookSummaryResponse>  | PUBLIC   | Featured/trending books             |

---

## Summary — API Count by Domain

| Domain          | Endpoints | Public | Customer | Admin |
|-----------------|-----------|--------|----------|-------|
| Authentication  | 5         | 3      | 2        | 0     |
| Users           | 8         | 0      | 6        | 2     |
| Categories      | 5         | 2      | 0        | 3     |
| Authors         | 5         | 2      | 0        | 3     |
| Publishers      | 5         | 2      | 0        | 3     |
| Books           | 6         | 4      | 0        | 2     |
| Search          | 1         | 1      | 0        | 0     |
| Wishlist        | 3         | 0      | 3        | 0     |
| Cart            | 5         | 0      | 5        | 0     |
| Orders          | 7         | 0      | 6        | 1     |
| Payments        | 5         | 0      | 4        | 1     |
| Shipping        | 3         | 1      | 1        | 1     |
| Reviews         | 6         | 1      | 2        | 3     |
| Coupons         | 5         | 1      | 0        | 4     |
| Recommendations | 2         | 1      | 1        | 0     |
| **Total**       | **71**    | **18** | **30**   | **23**|
