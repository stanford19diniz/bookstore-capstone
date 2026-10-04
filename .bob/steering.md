# IBM BOB — Steering File
# eCommerce Bookstore Capstone Project

## Project Overview
An eCommerce bookstore backend built using spec-driven, AI-augmented development with IBM BOB.
This is a capstone project for the IBM Applied AI Specialist – Cloud FullStack program.

## Goal
Build a fully functional Spring Boot 3 REST API backend for an online bookstore,
covering browsing, cart, orders, payments, and shipment — deployed locally and optionally to AWS.

## Technology Stack
| Layer            | Technology                          |
|------------------|-------------------------------------|
| Language         | Java 17                             |
| Framework        | Spring Boot 3.x                     |
| Build Tool       | Maven                               |
| Database         | PostgreSQL 15+ (Supabase free tier — cloud hosted) |
| ORM              | Spring Data JPA / Hibernate 6       |
| Security         | Spring Security + JWT               |
| Migrations       | Flyway                              |
| API Spec         | OpenAPI 3.1 (openapi.yaml)          |
| Code Gen         | OpenAPI Generator                   |
| Mapping          | MapStruct                           |
| Boilerplate      | Lombok                              |
| Testing          | JUnit 5, Mockito, AssertJ, Testcontainers |
| Containerization | Docker + docker-compose             |
| Cloud (optional) | AWS App Runner, ECR, RDS, Secrets Manager |
| CI/CD (optional) | GitHub Actions                      |

## Why Java 17 (not 21)
Java 17 is the minimum baseline for Spring Boot 3.x and is installed on the local development machine.
Java 21 bytecode cannot run on a Java 17 JVM. All Maven compiler settings must target Java 17.

## Database Strategy
- **Provider:** Supabase (https://supabase.com) — free tier, PostgreSQL 15, cloud hosted. No local DB install required.
- Project: `stanford19diniz's Project` (NANO compute, free tier)
- Connection string stored via environment variable `DATABASE_URL` — never hardcoded in source.
- `application-local.yml` holds the actual Supabase JDBC URL for local dev runs (git-ignored via .gitignore).
- Supabase free tier auto-pauses after 1 week of inactivity — resume from the dashboard before running the app.
- Flyway manages all schema migrations; no manual SQL execution in Supabase SQL editor needed.
- For CI/CD integration tests, use an H2 in-memory DB profile to avoid dependency on the cloud DB.

## Domain Modules
1. **Member**   — Guest/Registered users, Login, Logout, Roles & Entitlements
2. **Store**    — Store management, Catalog creation, Store Policies
3. **Catalog**  — Books, Categories, Authors, Publishers, Search, Recommendations
4. **Order**    — Cart, Checkout, Order lifecycle, History, Coupons, Gift Points
5. **Payment**  — Gateway integration, Refunds, Confirmation, Wallet, Gift cards
6. **Shipping** — Rate calculation, Delivery time estimation, Return shipment

## Architecture Flow
Registered/Guest User → User Login → Home Page → Browse → Add to Cart → Payment Processing → Shipment

## Coding Conventions
- Package root: `com.bookstore`
- Layered architecture: `controller → service → repository → entity`
- UUID primary keys on all entities
- Audit columns on all tables: `created_at`, `created_by`, `updated_at`, `updated_by`, `version`, `is_deleted`
- Soft delete support everywhere
- Optimistic locking via `@Version`
- REST naming: plural nouns, kebab-case URIs (e.g. `/api/v1/books`, `/api/v1/order-items`)
- HTTP verbs strictly: GET (read), POST (create), PUT (full update), PATCH (partial), DELETE (soft)
- All responses wrapped in a standard envelope: `{ "data": ..., "meta": ..., "errors": [] }`
- Problem Details (RFC 7807) for all error responses
- JWT Bearer token authentication; roles: GUEST, CUSTOMER, ADMIN
- Pagination: `page`, `size`, `sort` query params on all list endpoints
- OpenAPI spec is the single source of truth — code must match spec exactly

## Key Business Rules
- Guest users can browse catalog but not checkout
- Registered users can manage wishlist, cart, orders
- Orders can be cancelled within 48 hours of placement
- Gift points can be redeemed at checkout
- Related/recommended products shown based on order history
- Products tagged with tentative delivery dates
- Payment supports: Credit card, Debit card, Gift card, Wallet

## File Structure
```
bookstore-backend/
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── mapper/
│   │   │   ├── security/
│   │   │   └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
├── spec/
│   ├── openapi.yaml
│   ├── domain-model.md
│   ├── data-model.md
│   └── requirements.md
├── prompts/
├── .bob/
│   └── steering.md
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## Spec-Driven Workflow
All development follows the 23-prompt progressive workflow:
1. Requirements → 2. Domain Model → 3. Data Model → 4. DDL
5. API Design → 6. OpenAPI Spec → 7. Governance Review
8. Project Structure → 9-16. Code Generation
17-18. Tests → 19-22. Docker + AWS + CI/CD → 23. Production Review

## Output Locations
- Requirements:     `spec/requirements.md`
- Domain model:     `spec/domain-model.md`
- Data model:       `spec/data-model.md`
- SQL DDL:          `spec/sql/`
- OpenAPI spec:     `spec/openapi.yaml`
- Spring Boot app:  `bookstore-backend/`
- Docker:           `docker-compose.yml`, `Dockerfile`
- CI/CD:            `.github/workflows/`
