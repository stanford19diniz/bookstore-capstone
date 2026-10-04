# eCommerce Bookstore — 23 Spec-Driven Prompts
# Source: GenAI_Bookstore_Sample_Prompt.docx
# Usage: Execute prompts in order using IBM BOB. Each output feeds the next prompt.

---

## Prompt 1 — Analyze Wireframes and Create Requirements

You are an Architect cum Developer. Analyze the bookstore wireframes and architecture diagrams provided.
Your task is to reverse engineer the complete backend requirements.

Produce:
1. Functional Requirements
2. Non-Functional Requirements
3. User Roles
4. User Stories
5. Business Capabilities
6. Domain Boundaries
7. Backend Modules
8. API Domains
9. Data Ownership Matrix
10. Assumptions
11. Out Of Scope Items

Use the architecture diagram and all UI screens. Do not generate APIs yet. Output in markdown.

---

## Prompt 2 — Create Event Storming and Domain Model

Using the requirements generated previously:
Act as a DDD (Domain Driven Design) architect.

Create:
1. Bounded Contexts
2. Aggregates
3. Entities
4. Value Objects
5. Domain Events
6. Commands
7. Read Models
8. Relationships
9. State Transitions

The system is an online bookstore supporting:
- Guest browsing
- User registration/login
- Catalog - Categories - Authors - Publishers
- Wishlist - Cart - Checkout
- Orders - Coupons - Reviews
- Recommendations - Shipment tracking - Payments

Provide a complete domain model. Output as markdown tables and mermaid diagrams.

---

## Prompt 3 — Create Physical Data Model

Using the domain model generated previously:
Act as a Senior Data Architect. Create a normalized relational database design.

Requirements:
- PostgreSQL
- Third Normal Form
- UUID primary keys
- Audit columns
- Soft delete support
- Optimistic locking support

Generate:
1. Entity Relationship Diagram
2. Tables
3. Columns
4. Primary Keys
5. Foreign Keys
6. Unique Constraints
7. Indexes

Return only database design documentation. Do not generate SQL yet.

---

## Prompt 4 — Generate SQL DDL Scripts

Using the physical data model generated previously:
Generate PostgreSQL DDL scripts.

Requirements:
- CREATE TABLE statements
- PK constraints
- FK constraints
- Indexes
- Unique constraints
- Audit fields

Use:
- created_at
- created_by
- updated_at
- updated_by
- version
- is_deleted

Generate files logically grouped as:
- 001_users.sql
- 002_catalog.sql
- 003_orders.sql
- 004_payments.sql
- 005_shipping.sql
- 006_reviews.sql
- 007_coupons.sql

Output complete executable SQL.

---

## Prompt 5 — Create API Resource Model

Using the requirements, domain model, and database schema:
Act as a Principal API Architect. Design REST APIs.

Group APIs into domains:
- Authentication
- Users
- Authors
- Publishers
- Categories
- Books
- Search
- Wishlist
- Cart
- Orders
- Payments
- Shipping
- Reviews
- Coupons
- Recommendations

For each API define:
- Resource Name
- HTTP Method
- URI
- Request DTO
- Response DTO
- Validation Rules
- Error Codes

Do not generate OpenAPI yet. Output as API design documentation.

---

## Prompt 6 — Generate OpenAPI Specification

Using the API design document:
Generate a complete OpenAPI 3.1 specification.

Requirements:
- Split by tags
- Reusable schemas
- Request models
- Response models
- Validation constraints
- JWT security
- Pagination
- Sorting
- Filtering

Include:
- Authentication APIs
- Catalog APIs
- Cart APIs
- Order APIs
- Payment APIs
- Shipping APIs
- Review APIs
- Wishlist APIs

Return a single valid openapi.yaml.

---

## Prompt 7 — Review OpenAPI

Review the generated OpenAPI specification.
Act as a Principal API Governance Architect.

Perform:
1. Design review
2. REST maturity review
3. Naming consistency review
4. Pagination review
5. Error handling review
6. Security review

Generate:
- Findings
- Recommended fixes
- Updated OpenAPI specification sections

Output a governance report.

---

## Prompt 8 — Generate Spring Boot Project Structure

Using the approved OpenAPI specification:
Create a Spring Boot 3 architecture.

Technology stack:
- Java 17
- Spring Boot 3.x
- Maven
- PostgreSQL
- Spring Security JWT
- Spring Data JPA
- Flyway
- OpenAPI Generator
- MapStruct
- Lombok
- Testcontainers

Generate:
1. Package structure
2. Layered architecture
3. Module structure
4. Maven dependencies
5. Design decisions

Do not generate code yet.

---

## Prompt 9 — Generate JPA Entities

Using:
- OpenAPI specification
- Database schema

Generate JPA entities.

Requirements:
- Hibernate 6
- UUID keys
- Relationships
- Auditing support
- Optimistic locking

Generate one entity per file. Output complete code.

---

## Prompt 10 — Generate Repositories

Using the JPA entities:
Generate Spring Data repositories.

Requirements:
- Pagination support
- Specifications support
- Query methods
- Custom search methods

Generate complete code.

---

## Prompt 11 — Generate DTOs and Mappers

Using the OpenAPI schemas:
Generate:
- Request DTOs
- Response DTOs
- MapStruct mappers

Requirements:
- Validation annotations
- Null safety
- Mapping rules

Output complete code.

---

## Prompt 12 — Generate Service Layer

Using:
- OpenAPI specification
- Repositories
- DTOs

Generate service layer implementations.

Requirements:
- Business validations
- Transactions
- Exception handling
- Logging

Generate services for:
- Authentication
- Catalog
- Wishlist
- Cart
- Orders
- Payments
- Shipping
- Reviews
- Coupons

Output complete code.

---

## Prompt 13 — Generate Controllers

Using the OpenAPI specification:
Generate Spring REST controllers.

Requirements:
- Follow OpenAPI exactly
- Bean validation
- Exception handling
- Swagger annotations

Generate complete controller classes.

---

## Prompt 14 — Generate Security

Generate Spring Security implementation.

Requirements:
- JWT Authentication
- Role Based Access Control

Roles:
- GUEST
- CUSTOMER
- ADMIN

Generate:
- SecurityConfig
- JwtFilter
- JwtProvider
- Authentication APIs
- Authorization Rules

Output complete code.

---

## Prompt 15 — Generate Global Exception Handling

Generate enterprise-grade exception handling.

Requirements:
- Problem Details (RFC 7807)
- Global exception handler
- Business exceptions
- Validation exceptions
- Security exceptions

Generate complete code.

---

## Prompt 16 — Generate Flyway Migrations

Using the PostgreSQL schema:
Generate Flyway migration scripts.

Requirements:
- V1__initial_schema.sql
- V2__seed_categories.sql
- V3__seed_books.sql
- V4__seed_authors.sql
- V5__seed_publishers.sql

Output complete scripts.

---

## Prompt 17 — Generate Unit Tests

Generate JUnit 5 tests.

Requirements:
- Service tests
- Repository tests
- Controller tests

Tools:
- Mockito
- AssertJ
- Testcontainers

Target minimum coverage: 80%

Generate complete test classes.

---

## Prompt 18 — Generate Integration Tests

Generate integration tests.

Requirements:
- PostgreSQL Testcontainer
- Full API tests
- Security tests
- Order workflow tests

Output complete code.

---

## Prompt 19 — Generate Dockerization

Generate deployment artifacts.

Create:
1. Dockerfile
2. docker-compose.yml
3. PostgreSQL container
4. Flyway startup
5. Health checks

Output complete files.

---

## Prompt 20 — Generate AWS Architecture

Using the Spring Boot application:
Design AWS deployment architecture.

Requirements:
- App Runner
- ECR
- RDS PostgreSQL
- Secrets Manager
- CloudWatch
- X-Ray

Generate:
1. Architecture Diagram
2. Resource List
3. Cost Estimate
4. Security Controls

Do not generate CloudFormation yet.

---

## Prompt 21 — Generate CloudFormation Templates

Generate production-ready AWS CloudFormation templates.

Create templates for:
- ECR Repository
- RDS PostgreSQL
- Secrets Manager
- App Runner Service
- IAM Roles
- CloudWatch Logs

Requirements:
- Parameterized
- Multi-environment support
- Secure defaults

Output complete YAML templates.

---

## Prompt 22 — Generate CI/CD Pipeline

Generate CI/CD pipeline.

GitHub Flow:
- main
- develop
- feature/*

Pipeline:
1. Build
2. Unit Test
3. Integration Test
4. Sonar
5. Docker Build
6. Push To ECR
7. Deploy To App Runner

Generate:
1. GitHub Actions workflow
2. Environment configuration
3. Secrets required
4. Rollback strategy

Output complete YAML.

---

## Prompt 23 — Generate Production Readiness Review

Act as a Principal Architect. Review the entire solution.

Evaluate:
- Security
- Scalability
- Reliability
- Performance
- Cost
- Observability
- Maintainability

Generate:
1. Architecture scorecard
2. Risks
3. Improvements
4. Production readiness checklist
