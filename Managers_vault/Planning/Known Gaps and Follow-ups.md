# Known Gaps and Follow-ups

These are observed from the code and configuration. They are not guesses.

## Security and Configuration

- `application.properties` contains hard-coded datasource and JWT settings. Move secrets to environment variables or a secret manager and rotate exposed values.
- `JwtAuthFilter` accepts tokens through the `token` query parameter. Query tokens can leak through browser history, logs, and referrers.
- `logging.level.org.springframework.security=DEBUG` is enabled in application properties. This is useful during development but noisy for production.
- `server.error.include-message=always` exposes exception messages to HTTP clients.

## API and Authorization

- Collective sales have create, invite, join, contribute, update, leave, and cancel endpoints, but no observed endpoint to complete a collective sale and set `sold_at`.
- `GET /api/getLast5Sales` blocks workers but does not scope manager requests to the manager's cooperative.
- Admin multiplier writes can pass a null `cooperativeId` through `determineTargetCooperativeForWrite`; the service currently lets admins through ownership validation before persistence.
- `GET /api/performance` accepts `startDate` and `endDate`, but the repository query does not use them.

## Data Mapping

- Many services map `Object[]` query results by numeric index. This is fragile when SQL columns change.
- Several repositories extend `JpaRepository<CooperativeEntity, Long>` even when the repository is not conceptually a cooperative repository.
- Some error messages mention unrelated domains, such as sales history catching a database error while "fetching notices".

## Stock and Sales

- Normal sale creation does not reserve stock. Stock is checked only on completion.
- `recordSale` returns zero for both missing stock rows and insufficient stock; the service returns a generic stock-row message.
- Collective sale cancellation and leave rely on contribution weights to return stock, which makes correctness depend on contribution updates being the only reservation path.

## Frontend

- `templates/login.html` and static tester pages contain Danish UI text. The vault is English, but the application UI is mixed-language.
- There are two collective sale pages: a Thymeleaf template and an older static tester page.
- Frontend JavaScript is embedded directly in templates.

## Testing

- Only one `@SpringBootTest` context test is observed.
- There are no observed unit or integration tests for authorization, stock accounting, sales lifecycles, reports, or scheduler behavior.
- Because JPA schema validation runs on startup, tests may require database availability unless test-specific configuration is added.

## Documentation

- `src/main/java/dk/aau/network_management_system/auth/auth.md` is an in-source authentication note. This vault now centralizes current documentation, but that source note may need alignment if it is still used by the team.

## Related Notes

- [[Operations/Build Test Deploy|Build, Test, and Deploy]]
- [[Architecture/Runtime and Security|Runtime and Security]]
- [[Domain/Collective Sales|Collective Sales]]
- [[Domain/Materials Stock and Measurements|Materials, Stock, and Measurements]]

