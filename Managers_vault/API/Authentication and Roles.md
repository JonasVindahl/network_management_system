# Authentication and Roles

## Login Endpoint

`POST /api/auth/login`

Request body:

```json
{
  "cpf": "worker-cpf",
  "password": "plain-text password"
}
```

Response body:

```json
{
  "token": "jwt"
}
```

Flow:

1. `AuthController` authenticates CPF and password through Spring Security.
2. `WorkerDetailsService` loads the worker row from `public.workers`.
3. `JwtUtil` generates a JWT with CPF, role, cooperative ID, and worker ID.
4. The client sends that token on later requests.

## Token Locations

`JwtAuthFilter` accepts JWTs in this order:

1. `Authorization: Bearer <token>`
2. query string parameter `token`
3. cookie named `jwt`

## Roles

Observed role codes:

| Code | Meaning | Typical access |
| --- | --- | --- |
| `A` | Admin | Cross-cooperative access when a cooperative ID is supplied where required. |
| `M` | Manager | Own-cooperative operational access. |
| `W` | Worker | Own worker and limited self-service gamification/productivity access. |

## Cooperative Scoping

Most manager endpoints ignore a requested cooperative and use the authenticated user's cooperative. Admin endpoints usually require `cooperativeId` when a cooperative-scoped read is requested.

`PermissionHelper.determineTargetCooperative` is the shared rule:

- worker or manager: return authenticated user's cooperative ID
- admin: require a request cooperative ID

`PermissionHelper.determineTargetWorker` is the shared worker rule:

- worker: return authenticated user's worker ID
- manager/admin: use requested worker ID

## Related Notes

- [[Architecture/Runtime and Security|Runtime and Security]]
- [[API Reference]]

