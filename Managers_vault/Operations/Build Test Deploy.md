# Build, Test, and Deploy

## Maven Commands

Build with tests:

```bash
./mvnw test
```

Package:

```bash
./mvnw package
```

Run locally:

```bash
./mvnw spring-boot:run
```

Package without tests, as used by the Dockerfile:

```bash
./mvnw package -DskipTests
```

## Docker Commands

Build image through Compose:

```bash
docker compose build
```

Start service:

```bash
docker compose up -d
```

Stop service:

```bash
docker compose down
```

## GitHub Actions

Workflow:

- `.github/workflows/deploy.yml`

Triggers:

- pull requests to `main`
- pushes to `main`

Jobs:

### `build-test`

- runner: self-hosted with labels `self-hosted` and `proxmox`
- checks out the code
- runs `docker compose build`
- supplies database and JWT environment variables from GitHub secrets

### `deploy`

- depends on `build-test`
- only runs on push events
- runner: self-hosted with labels `self-hosted` and `proxmox`
- checks out the code
- runs:
  - `docker compose build`
  - `docker compose up -d`
- supplies the same database and JWT environment variables from GitHub secrets

## Tests

Observed test file:

- `src/test/java/dk/aau/network_management_system/NetworkManagementSystemApplicationTests.java`

The test uses `@SpringBootTest` and has one `contextLoads` test.

Because the application validates the database schema at startup, context tests may require reachable PostgreSQL configuration unless test-specific datasource configuration is added.

## Related Notes

- [[Setup and Configuration]]
- [[Runbook]]
- [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]]

