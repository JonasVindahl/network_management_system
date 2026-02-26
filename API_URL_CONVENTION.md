# API URL Design Convention

## Core Principle

All API calls follow the same structure:

```
/api/{resource}/{sub-resource}[?{scope}={id}][&{filters}]
```

Resources and sub-resources are **nouns**, never verbs.
Scope and filters are **query parameters**, never path segments — unless they are genuinely required to identify the resource (e.g. a notice ID).

---

## Identity & Scope Resolution

The system resolves **who is asking** and **on whose behalf** from two sources:

| Source              | Resolved from                  | Who uses it          |
|---------------------|-------------------------------|----------------------|
| Caller identity     | CPF claim inside JWT token     | All roles            |
| `?workerId=`        | Query parameter                | Managers only        |
| `?cooperativeId=`   | Query parameter                | Admins only          |

### Worker (default role)
No scope parameters are needed.
`cooperative_id` and `worker_id` are derived automatically from the JWT.

```
GET /api/analytics/performance
    → uses cooperative from JWT
```

### Manager
`cooperative_id` is always derived from the JWT (a manager belongs to one cooperative).
`?workerId=` can be added to query a specific worker within their cooperative.

```
GET /api/analytics/workers/productivity
    → all workers in manager's cooperative (from JWT)

GET /api/analytics/workers/productivity?workerId=12
    → specific worker in manager's cooperative
```

### Admin (System Administrator)
`?cooperativeId=` overrides the JWT-based cooperative lookup.
`?workerId=` can be combined freely.

```
GET /api/analytics/workers/productivity?cooperativeId=5
    → all workers in cooperative 5

GET /api/analytics/workers/productivity?cooperativeId=5&workerId=12
    → specific worker in a specific cooperative
```

---

## URL Structure

```
/api/{resource}/{sub-resource}[/{identifier}][?cooperativeId=][&workerId=][&filters...]
        │              │               │
        │              │               └─ Only for direct resource lookup (e.g. /notices/42)
        │              └─ Optional grouping of related actions
        └─ Domain area: analytics, notices, leaderboard, materials, sales...
```

### When to use `/{identifier}` vs `?param=`

| Use case                               | Pattern                           |
|----------------------------------------|-----------------------------------|
| Fetch/update/delete **one** resource   | `/{id}` path segment              |
| Scope a query to a cooperative         | `?cooperativeId=` query param     |
| Scope a query to a worker              | `?workerId=` query param          |
| Filter results (date range, priority)  | `?startDate=&endDate=` etc.       |

---

## Full Endpoint Reference (Proposed Naming)

### Authentication

| Method   | Path             | Auth | Description            |
|---------|------------------|------|------------------------|
| `POST`  | `/api/auth/login` | No   | Login, receive JWT     |

---

### Notice Board

| Method   | Path                     | Scope params      | Filters           | Description                     |
|---------|--------------------------|-------------------|-------------------|---------------------------------|
| `GET`   | `/api/notices`           | `?cooperativeId=` | `?priority=`      | All active notices in scope     |
| `GET`   | `/api/notices/{id}`      | —                 | —                 | Single notice                   |
| `POST`  | `/api/notices`           | —                 | —                 | Create notice (Manager/Admin)   |
| `PUT`   | `/api/notices/{id}`      | —                 | —                 | Update notice (Manager/Admin)   |
| `DELETE`| `/api/notices/{id}`      | —                 | —                 | Delete notice (Manager/Admin)   |

**Scope behaviour for `GET /api/notices`:**

| Caller  | No params              | `?cooperativeId=5`         | `?priority=2`                 |
|---------|------------------------|----------------------------|-------------------------------|
| Worker  | Own coop + global      | 403 Forbidden              | Own coop filtered             |
| Manager | Own coop + global      | 403 Forbidden              | Own coop filtered             |
| Admin   | All global notices     | Specific coop + global     | All, filtered by priority     |

---

### Analytics

| Method | Path                             | Scope params                   | Filters                    | Description                  |
|--------|----------------------------------|--------------------------------|----------------------------|------------------------------|
| `GET`  | `/api/analytics/performance`     | `?cooperativeId=` (admin)      | —                          | Cooperative performance      |
| `GET`  | `/api/analytics/workers`         | `?workerId=` `?cooperativeId=` | `?startDate=` `?endDate=`  | Worker productivity          |
| `GET`  | `/api/analytics/stock`           | `?cooperativeId=` (admin)      | —                          | Stock by material             |
| `GET`  | `/api/analytics/revenue`         | `?cooperativeId=` (admin)      | `?startDate=` `?endDate=`  | Revenue & sales data         |

**Scope behaviour for `GET /api/analytics/workers`:**

| Caller  | No params              | `?workerId=12`          | `?cooperativeId=5`         | `?cooperativeId=5&workerId=12` |
|---------|------------------------|-------------------------|----------------------------|---------------------------------|
| Worker  | Own data only          | 403 Forbidden           | 403 Forbidden              | 403 Forbidden                   |
| Manager | All in own coop        | Specific worker, own coop | 403 Forbidden            | 403 Forbidden                   |
| Admin   | 400 (coop required)    | 400 (coop required)     | All workers in coop 5      | Specific worker in coop 5       |

---

### Leaderboard

| Method | Path                  | Scope params              | Description               |
|--------|-----------------------|---------------------------|---------------------------|
| `GET`  | `/api/leaderboard`    | `?cooperativeId=` (admin) | Top 3 workers by XP       |

**Scope behaviour:**

| Caller  | No params       | `?cooperativeId=5` |
|---------|-----------------|--------------------|
| Worker  | Own cooperative | 403 Forbidden      |
| Manager | Own cooperative | 403 Forbidden      |
| Admin   | 400 required    | Cooperative 5      |

---

### Materials & Sales

| Method | Path                      | Scope params | Filters         | Description               |
|--------|---------------------------|--------------|-----------------|---------------------------|
| `GET`  | `/api/materials/{id}/sales` | —          | `?limit=5`      | Recent sales for material |
| `POST` | `/api/materials/multipliers` | —         | —               | Create/update multiplier  |
| `GET`  | `/api/materials/{id}/multiplier` | `?cooperativeId=` (admin) | — | Get multiplier for material |

---

## Scope Parameter Summary

| Parameter        | Who can use it | Meaning                                       |
|-----------------|----------------|-----------------------------------------------|
| *(none)*         | All roles      | Resolve context from JWT (own coop/worker)    |
| `?workerId=`     | Manager, Admin | Query on behalf of a specific worker          |
| `?cooperativeId=`| Admin only     | Query a specific cooperative                  |

Both can be combined by Admins:
```
GET /api/analytics/workers?cooperativeId=5&workerId=12&startDate=2025-01-01T00:00:00
```

---

## Date Filter Format

All date filters use **ISO-8601** format:

```
?startDate=2025-01-01T00:00:00
?endDate=2025-01-31T23:59:59
```

Both are always optional. Defaults are defined per endpoint (e.g. last 30 days, last 12 months).

---

## HTTP Status Codes

| Status | Meaning                                                                 |
|--------|-------------------------------------------------------------------------|
| `200`  | OK                                                                      |
| `201`  | Resource created                                                        |
| `204`  | Deleted, no content returned                                            |
| `400`  | Bad request (missing required param, failed validation)                 |
| `401`  | Missing or expired JWT                                                  |
| `403`  | Valid JWT but insufficient role for this scope (e.g. worker using `?cooperativeId=`) |
| `404`  | Resource not found                                                      |
| `500`  | Server error                                                            |

---

## Summary of the Pattern

```
                    ┌─────────────────────── Derived from JWT (always)
                    │         ┌───────────── Manager can add this to target a worker
                    │         │         ┌─── Admin can add this to target a cooperative
                    ▼         ▼         ▼
GET /api/analytics/workers?workerId=12&cooperativeId=5&startDate=...&endDate=...
     └───────────────────┘ └────────────────────────┘ └─────────────────────────┘
           Resource path          Scope params                 Filter params
```

Rules in one sentence:
> **Path identifies what you want. Scope params say whose data you want. Filter params narrow the result set.**
