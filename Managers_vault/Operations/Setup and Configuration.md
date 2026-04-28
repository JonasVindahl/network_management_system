# Setup and Configuration

## Requirements

Observed from the repository:

- Java 25
- Maven wrapper (`mvnw`, `mvnw.cmd`)
- PostgreSQL
- Docker and Docker Compose for container deployment

## Local Application Configuration

File:

- `src/main/resources/application.properties`

Observed settings:

- application name: `network-management-system`
- datasource URL, username, and password are configured directly in the properties file
- PostgreSQL driver: `org.postgresql.Driver`
- Hibernate DDL mode: `validate`
- SQL logging enabled
- JWT secret and expiration configured
- Spring Security debug logging enabled
- server error messages included in HTTP responses
- Thymeleaf cache disabled
- Thymeleaf templates loaded from `classpath:/templates/`
- static resources loaded from `classpath:/static/`

Security note:

- The file contains hard-coded local datasource and JWT values. Treat those values as secrets and rotate them if this repository is shared.

## Docker Configuration

Files:

- `Dockerfile`
- `docker-compose.yml`

Dockerfile behavior:

1. Build stage uses `eclipse-temurin:25-jdk`.
2. Copies Maven wrapper and `pom.xml`.
3. Runs `./mvnw dependency:go-offline -q`.
4. Copies `src`.
5. Packages the jar with tests skipped.
6. Runtime stage uses `eclipse-temurin:25-jre`.
7. Runs `java -jar app.jar`.
8. Exposes port `8080`.

Docker Compose behavior:

- builds the app service from the repository root
- restarts unless stopped
- maps host port `8080` to container port `8080`
- passes datasource and JWT settings through environment variables:
  - `DB_HOST`
  - `DB_USER`
  - `DB_PASSWORD`
  - `JWT_SECRET`

## Database Setup

Schema file:

- `Database/DMS_db_schema.sql`

The application uses `spring.jpa.hibernate.ddl-auto=validate`, so the database schema must already exist before the app starts.

High-level setup:

1. Create a PostgreSQL database.
2. Run `Database/DMS_db_schema.sql`.
3. Configure datasource values.
4. Start the application.

## OpenAPI

Swagger/OpenAPI dependency:

- `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6`

Public paths:

- `/swagger-ui/**`
- `/v3/api-docs/**`

The OpenAPI configuration defines bearer JWT auth as `BearerAuth`.

## Related Notes

- [[Build Test Deploy]]
- [[Runbook]]
- [[Architecture/Runtime and Security|Runtime and Security]]

