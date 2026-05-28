# Blog API

REST API for a blog application built with Spring Boot 4 and Spring Security 6.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Security 6 (JWT planned)
- Spring Data JPA + Hibernate
- PostgreSQL 16 (in Docker)
- Flyway for database migrations
- Lombok
- Maven

## Features

- User registration with validation
- User login (Basic Auth, JWT in progress)
- Role-based access control (USER, ADMIN)
- BCrypt password hashing
- Database migrations via Flyway

## Getting Started

### Prerequisites

- Java 21+
- Docker Desktop
- Maven (or use included `mvnw`)

### Run

1. Start PostgreSQL:
```bash
   docker compose up -d
```

2. Run the application:
```bash
   ./mvnw spring-boot:run
```

3. The API will be available at `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login | No |
| GET | `/api/public/hello` | Public endpoint | No |
| GET | `/api/private/hello` | Private endpoint | Yes |
| GET | `/api/admin/hello` | Admin only | Yes + ADMIN role |

## API Documentation

Interactive API documentation (Swagger UI) is available at:
http://localhost:8080/swagger-ui.html

To test protected endpoints:
1. Use `/api/auth/login` to get a JWT token
2. Click **Authorize** button at the top right
3. Paste the token (without "Bearer" prefix)
4. All subsequent requests will include the token automatically# Blog REST API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6-green.svg)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-yellow.svg)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

A production-style REST API for a blogging platform, built with Spring Boot. It features stateless JWT authentication, role- and resource-based authorization, posts with comments, pagination, database migrations, and interactive API documentation.

This project was built to demonstrate clean backend architecture and the core skills required for a Java/Spring backend role.

---

## Features

- **JWT Authentication** — stateless auth with registration and login; tokens carry user identity and role.
- **Role-based authorization** — `USER` and `ADMIN` roles enforced via Spring Security.
- **Resource-based authorization** — users can edit only their own posts; admins can delete any post or comment for moderation.
- **Posts CRUD** — create, read, update, and delete blog posts.
- **Comments** — post-scoped comments with their own authorization rules.
- **Pagination & sorting** — list endpoints support `page`, `size`, and `sort` query parameters.
- **Database migrations** — schema is versioned and applied automatically with Flyway.
- **Global error handling** — consistent JSON error responses with meaningful HTTP status codes.
- **API documentation** — interactive Swagger UI with JWT support.
- **Unit tests** — service layer covered with JUnit 5 and Mockito.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security 6, JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Documentation | springdoc-openapi (Swagger UI) 2.6.0 |
| Build | Maven |
| Containerization | Docker Compose |
| Testing | JUnit 5, Mockito |
| Utilities | Lombok, Bean Validation |

---

## Architecture

The codebase is organized by feature (package-by-feature), keeping related classes together:

```
src/main/java/org/example/blogapi/
├── auth/          # Registration, login, auth DTOs
├── comment/       # Comment entity, service, controller, DTOs
├── config/        # Security and OpenAPI configuration
├── exception/     # Global exception handler and custom exceptions
├── post/          # Post entity, service, controller, DTOs, mapper
├── security/      # JWT service and authentication filter
└── user/          # User entity, role, repository, UserDetailsService
```

**Request flow:** an incoming request passes through the `JwtAuthFilter`, which validates the bearer token and populates the Spring Security context. Controllers delegate to services, which contain the business logic and authorization checks. Services use repositories for data access and mappers to convert entities into DTOs, so internal entities (including password hashes) are never exposed to clients.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/walwency/blog-api.git
cd blog-api
```

### 2. Start the database

The PostgreSQL database runs in Docker. Start it with:

```bash
docker compose up -d
```

This launches a `postgres:16-alpine` container named `blog-db` on port `5432`, pre-configured with the database, user, and password the application expects.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

On startup, Flyway automatically applies the schema migrations. The API is then available at `http://localhost:8080`.

### 4. Explore the API

Open the interactive Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

Base URL: `http://localhost:8080`

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register a new user, returns a JWT | Public |
| `POST` | `/api/auth/login` | Log in, returns a JWT | Public |

### Posts

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/posts` | List posts (paginated, sortable) | Public |
| `GET` | `/api/posts/{id}` | Get a single post | Public |
| `POST` | `/api/posts` | Create a post | Authenticated |
| `PUT` | `/api/posts/{id}` | Update a post | Author only |
| `DELETE` | `/api/posts/{id}` | Delete a post | Author or Admin |

### Comments

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/posts/{postId}/comments` | List comments for a post (paginated) | Public |
| `POST` | `/api/posts/{postId}/comments` | Add a comment to a post | Authenticated |
| `DELETE` | `/api/comments/{id}` | Delete a comment | Author or Admin |

### Pagination & Sorting

List endpoints accept standard query parameters:

```
GET /api/posts?page=0&size=10&sort=createdAt,desc
```

---

## Usage Example

**1. Register or log in to obtain a token:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "petr", "password": "secret123"}'
```

Response:

```json
{
  "message": "Logged in successfully",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**2. Use the token to create a post:**

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "My first post", "content": "Hello, world!"}'
```

---

## Authorization Model

The API distinguishes between two kinds of authorization:

- **Role-based** — handled by Spring Security based on the user's role (`USER` / `ADMIN`).
- **Resource-based** — handled in the service layer based on ownership.

| Action | USER (owner) | USER (not owner) | ADMIN |
|--------|:------------:|:----------------:|:-----:|
| Update post | ✅ | ❌ | ❌ |
| Delete post | ✅ | ❌ | ✅ |
| Delete comment | ✅ | ❌ | ✅ |

> **Design note:** admins can *delete* content for moderation but cannot *edit* posts they don't own — this preserves authorship integrity.

---

## Testing

The service layer is covered by unit tests using JUnit 5 and Mockito. Tests mock repositories and mappers to verify business logic in isolation, covering happy paths and edge cases (resource not found, unauthorized access, admin override).

Run the tests with:

```bash
./mvnw test
```
---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

