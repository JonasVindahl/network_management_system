# Runtime and Security

## Runtime Entry Point

`src/main/java/dk/aau/network_management_system/NetworkManagementSystemApplication.java` starts Spring Boot with `SpringApplication.run(...)`.

The Maven project uses Java 25 and Spring Boot 3.5.11.

## Security Filter Chain

`SecurityConfig` configures:

- custom `WorkerDetailsService`
- JWT filter before `UsernamePasswordAuthenticationFilter`
- form login disabled
- CSRF disabled
- logout handler that removes the `jwt` cookie and redirects to `/login`
- BCrypt password encoder

Public routes observed in `SecurityConfig`:

- `test.html`
- `login.html`
- `/login`
- `/css/**`
- `/js/**`
- `/images/**`
- `/api/auth/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/error`

Authenticated routes:

- `/web/**`
- `/api/**`
- all other routes not explicitly permitted

## JWT Handling

`JwtUtil` signs tokens using `jwt.secret` and `jwt.expiration`.

JWT claims:

- subject: worker CPF
- `role`: role from `workers.user_type`
- `cooperativeId`: worker cooperative ID
- `workerId`: worker ID
- issued-at timestamp
- expiration timestamp

`JwtAuthFilter` accepts tokens from three places:

- `Authorization: Bearer <token>`
- query parameter named `token`
- cookie named `jwt`

When a token is valid, the filter creates a `JwtPrincipal` and a Spring Security authentication with authority `ROLE_<role>`.

## Roles

The code treats `workers.user_type` values as:

- `A`: admin
- `M`: manager
- `W`: worker

`AuthenticatedUser` reads the current `JwtPrincipal` from the Spring Security context and exposes:

- `getCpf()`
- `getRole()`
- `getCooperativeId()`
- `getWorkerId()`
- `isAdmin()`
- `isManager()`
- `isWorker()`

## Permission Helper

`PermissionHelper` centralizes common authorization decisions:

- `requireManagerOrAdmin()`: rejects workers.
- `requireAdmin()`: accepts admins only.
- `determineTargetCooperative(requestedCooperativeId)`: workers and managers always use their own cooperative; admins must provide a cooperative ID.
- `determineTargetCooperativeForWrite(dtoCooperativeId)`: managers write to their own cooperative; admins use the DTO value.
- `validateCooperativeOwnership(cooperativeId)`: admins pass; managers and workers must match their cooperative.
- `determineTargetWorker(requestedWorkerId)`: workers are forced to their own worker ID.

## Worker Credential Loading

`WorkerDetailsService` queries `public.workers` by CPF stored as `bytea`:

- `cpf`
- `password`
- `user_type`

It converts stored `password` bytes to a string and returns a Spring Security `User` with role `user_type.trim()`. `getWorkerInfo` retrieves `worker_id`, `cooperative`, and `user_type` for JWT claims.

## OpenAPI Security

`OpenApiConfig` registers a bearer JWT security scheme named `BearerAuth`.

## Observed Security Notes

- `application.properties` includes hard-coded local datasource and JWT settings. Docker Compose can override datasource and JWT values through environment variables.
- `JwtAuthFilter` permits token input through query parameters, which makes token leakage through logs or browser history possible.
- `AuthenticatedUser` assumes the principal is a `JwtPrincipal`; code that calls it outside an authenticated JWT request may fail.
- Static tester pages exist under `src/main/resources/static`.

## Related Notes

- [[API/Authentication and Roles|Authentication and Roles]]
- [[Operations/Setup and Configuration|Setup and Configuration]]
- [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]]

