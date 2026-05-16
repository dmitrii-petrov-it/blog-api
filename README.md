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

## Project Structure