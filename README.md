# DSA Visualizer Backend

Backend service for the Codeloom DSA Visualizer.

The project provides the backend infrastructure for user authentication, algorithm visualization, execution sessions, and event-driven processing.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Redis
- Apache Kafka
- Maven
- Docker / Docker Compose

## Architecture

The backend follows a modular architecture designed around:

- REST APIs
- JWT-based authentication
- PostgreSQL for persistent data
- Redis for caching and fast-access data
- Kafka for asynchronous event processing
- Flyway for database migrations

## Current Features

- User registration
- BCrypt password hashing
- User roles
- Login authentication
- JWT access tokens
- JWT authentication filter
- Protected API endpoints
- Global exception handling
- PostgreSQL integration
- Flyway database migrations
- Redis infrastructure
- Kafka infrastructure

## Project Structure

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/codeloom/dsa/
│   │   └── resources/
│   │       └── db/migration/
│   └── test/
├── .gitignore
├── pom.xml
├── mvnw
└── mvnw.cmd
