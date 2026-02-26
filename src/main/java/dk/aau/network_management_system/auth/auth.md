# Authentication Documentation

## Overview

This project uses **JWT (JSON Web Token)** based authentication via Spring Security.
Workers authenticate using their **CPF** (Brazilian tax ID) and **password**.
All endpoints except `/api/auth/login` require a valid JWT token.

---

## Tech Stack

- Spring Security 6.x
- JJWT 0.12.6
- BCrypt password hashing
- PostgreSQL (`bytea` for CPF, PIS, RG, and password columns)

---

## Project Structure

```
dk.aau.network_management_system.auth/
  AuthController.java        # Login endpoint
  AuthRequest.java           # Login request DTO (cpf, password)
  AuthResponse.java          # Login response DTO (token)
  JwtUtil.java               # Token generation and validation
  JwtAuthFilter.java         # Per-request JWT filter
  SecurityConfig.java        # Spring Security configuration
  WorkerDetailsService.java  # Loads worker from DB by CPF
```

---

## Login

### `POST /api/auth/login`

Public endpoint — no token required.

**Request body:**
```json
{
  "cpf": "testcpf123",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**curl example:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"cpf": "testcpf123", "password": "password"}'
```

---

## Using the Token

Include the token in the `Authorization` header for all protected requests:

```
Authorization: Bearer <token>
```

**curl example:**
```bash
curl "http://localhost:8080/getTop3" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Without a valid token, the server returns `403 Forbidden`.

---

## Token Contents (Claims)

The JWT payload contains:

| Claim | Description           | Example        |
|-------|-----------------------|----------------|
| `sub` | CPF of the worker     | `testcpf123`   |
| `role`| Worker's user_type    | `W` or `A`     |
| `iat` | Issued at (timestamp) | `1772094660`   |
| `exp` | Expiry (timestamp)    | `1772181060`   |

You can inspect a token at [jwt.io](https://jwt.io).

**Default expiry:** 24 hours (`86400000` ms, configurable in `application.properties`)

---

## User Types

| user_type | Role    | Description       |
|-----------|---------|-------------------|
| `W`       | Worker  | Standard worker   |
| `A`       | Admin   | Administrator     |

---

## Configuration

In `application.properties`:

```properties
jwt.secret=<base64-encoded-secret>
jwt.expiration=86400000
```

Generate a secure secret with:
```bash
openssl rand -base64 32
```

---

## How Authentication Works

```
1. Client sends POST /api/auth/login with CPF + password
2. Spring Security validates credentials against DB via WorkerDetailsService
3. BCrypt compares the submitted password against the stored hash
4. If valid, JwtUtil generates a signed token with CPF and role as claims
5. Client stores the token and sends it as Authorization: Bearer <token>
6. JwtAuthFilter validates the token on every subsequent request
7. If valid, the worker's identity is set in the SecurityContext
```

---

## Accessing the Authenticated Worker

In any protected controller method, inject `UserDetails` to get the logged-in worker:

```java
@GetMapping("/someEndpoint")
public Object someEndpoint(@AuthenticationPrincipal UserDetails userDetails) {
    String cpf = userDetails.getUsername(); // the worker's CPF
    // query DB using cpf to get cooperative, worker_id, etc.
}
```

This allows scoping queries to the worker's own cooperative without passing IDs as parameters.

---

## Role-Based Access

To restrict an endpoint to admins only, check the role:

```java
boolean isAdmin = userDetails.getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ROLE_A"));

if (!isAdmin) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins only");
}
```

Or enable method-level security by adding `@EnableMethodSecurity` to `SecurityConfig` and using:

```java
@PreAuthorize("hasRole('A')")
@GetMapping("/admin/something")
public Object adminEndpoint() { ... }
```

---

## Password Storage

Passwords must be **BCrypt encoded** before storing in the database.

To hash a password for direct SQL insertion:
```sql
-- The hash below is BCrypt of "password"
UPDATE public.workers
SET password = convert_to('$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'UTF8')
WHERE cpf = convert_to('testcpf123', 'UTF8');
```

When creating workers programmatically, encode with:
```java
new BCryptPasswordEncoder().encode("plaintextPassword");
```

---

## Notes

- CPF, PIS, RG, and password are stored as `bytea` in PostgreSQL — Java handles encoding/decoding using `UTF-8`
- The `user_type` column is `CHAR(1)` — always call `.trim()` when reading it in Java
- Tokens are stateless — there is no server-side session
- Tokens are not invalidated on logout unless a token blacklist is implemented