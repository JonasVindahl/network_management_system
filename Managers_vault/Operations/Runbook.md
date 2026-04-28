# Runbook

## Start Locally

1. Ensure PostgreSQL is running and has the schema from `Database/DMS_db_schema.sql`.
2. Confirm datasource and JWT settings in `application.properties` or environment variables.
3. Run:

```bash
./mvnw spring-boot:run
```

4. Open:

```text
http://localhost:8080/login
```

## Start with Docker Compose

1. Export required variables:

```bash
export DB_HOST=...
export DB_USER=...
export DB_PASSWORD=...
export JWT_SECRET=...
```

2. Build and start:

```bash
docker compose up -d --build
```

3. Check logs:

```bash
docker compose logs -f app
```

## Login and Token Use

1. POST CPF/password to `/api/auth/login`.
2. Use the returned token as:

```text
Authorization: Bearer <token>
```

The filter also accepts `token=<token>` and a `jwt` cookie.

## Useful Pages

- `/login`: login screen
- `/frontend`: manager dashboard
- `/normal-sale`: normal sales
- `/collective-sale`: collective sales
- `/swagger-ui/index.html`: Swagger UI
- `/test.html`: static API tester

## Scheduled Job Checks

Monthly random multipliers:

- table: `cooperative_random_multiplier`
- expected update on first day of month at 00:00

Achievements and levels:

- tables: `worker_achievement`, `worker_level`
- expected update daily at 02:00

Leaderboard:

- tables: `leaderboard_snapshot`, `leaderboard_entry`
- expected update at 03:00 on the 1st, 7th, 14th, 21st, and 28th as described in [[Architecture/Scheduled Jobs|Scheduled Jobs]]

## Common Failure Areas

Database startup:

- Hibernate runs in `validate` mode.
- Missing tables or mismatched column names can prevent app startup.

Authentication:

- CPF and password are stored as `bytea` in the schema.
- `WorkerDetailsService` converts bytes to strings before authentication.
- Password values must be BCrypt-compatible for Spring Security authentication.

Stock operations:

- Normal sale completion requires an existing stock row with enough stock.
- Collective contribution increases require enough current stock.
- Collective material changes fail while participants have positive contribution weights.

PDF generation:

- Report endpoints call JSON report services first.
- Access failures or missing report data will block PDF generation.

## Related Notes

- [[Setup and Configuration]]
- [[Build Test Deploy]]
- [[API/API Reference|API Reference]]

