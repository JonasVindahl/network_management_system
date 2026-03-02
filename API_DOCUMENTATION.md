# Network Management System — API Documentation

> **Base URL:** `http://192.168.255.184:8080` (or wherever the Spring Boot app is running)
> **Auth:** JWT Bearer token — include `Authorization: Bearer <token>` in all protected requests.
> **Content-Type:** `application/json`

---

## Table of Contents

1. [Authentication](#1-authentication)
2. [Notice Board](#2-notice-board)
3. [Cooperative Material Multipliers](#3-cooperative-material-multipliers)
4. [Cooperative Analytics](#4-cooperative-analytics)
5. [Leaderboard](#5-leaderboard)
6. [Sales History](#6-sales-history)
7. [Data Models](#7-data-models)
8. [Error Responses](#8-error-responses)

---

## 1. Authentication

### POST `/api/auth/login`

Authenticates a worker using CPF and password. Returns a JWT token valid for 24 hours.

**Auth required:** No

**Request body:**

```json
{
  "cpf": "string",
  "password": "string"
}
```

**Response `200 OK`:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Notes:**
- The token must be sent as `Authorization: Bearer <token>` on all subsequent requests.
- Token expiry: 86 400 000 ms (24 hours).
- The token payload contains the worker's CPF (subject) and role (claim).

---

## 2. Notice Board

### GET `/api/notices/all`

Returns all active **global** notices (not tied to any specific cooperative, not expired).

**Auth required:** Yes

**Response `200 OK`:**

```json
[
  {
    "noticeId": 1,
    "title": "System Maintenance",
    "content": "The system will be down on Sunday.",
    "priority": 2,
    "createdBy": 42,
    "createdAt": "2025-02-20T10:00:00Z",
    "lastUpdated": "2025-02-20T10:00:00Z",
    "expiresAt": "2025-03-01T00:00:00Z",
    "cooperativeId": null
  }
]
```

---

### GET `/api/notices/cooperative/{cooperativeId}`

Returns all active notices for a specific cooperative **plus** all active global notices.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description        |
|----------------|--------|--------------------|
| `cooperativeId` | `Long` | Cooperative ID     |

**Response `200 OK`:** Array of [Notice](#notice) objects.

---

### GET `/api/notices/{noticeId}`

Returns a single notice by ID.

**Auth required:** Yes

**Path parameters:**

| Parameter  | Type   | Description |
|-----------|--------|-------------|
| `noticeId` | `Long` | Notice ID   |

**Response `200 OK`:** [Notice](#notice) object.

**Response `404 Not Found`:** Notice does not exist.

---

### GET `/api/notices/filter/{priority}`

Returns all active notices filtered by priority level.

**Auth required:** Yes

**Path parameters:**

| Parameter  | Type  | Description                   |
|-----------|-------|-------------------------------|
| `priority` | `int` | Priority level: `1`, `2`, or `3` |

**Response `200 OK`:** Array of [Notice](#notice) objects.

---

### POST `/api/notices`

Creates a new notice. Only managers should call this endpoint (enforced at the application level).

**Auth required:** Yes (Manager)

**Request body:**

```json
{
  "title": "Important Update",
  "content": "Please read the new guidelines.",
  "priority": 1,
  "createdBy": 42,
  "expiresAt": "2025-12-31T23:59:59Z",
  "cooperativeId": 5
}
```

| Field           | Type      | Required | Constraints                              |
|----------------|-----------|----------|------------------------------------------|
| `title`         | `string`  | Yes      | Non-blank                                |
| `content`       | `string`  | Yes      | Non-blank                                |
| `priority`      | `integer` | Yes      | `1` (low), `2` (medium), or `3` (high)  |
| `createdBy`     | `Long`    | Yes      | Worker ID of the notice creator          |
| `expiresAt`     | `Instant` | Yes      | Must be a future timestamp (ISO-8601)    |
| `cooperativeId` | `Long`    | No       | Omit or set `null` for a global notice  |

**Response `201 Created`:** [Notice](#notice) object.

---

### PUT `/api/notices/{noticeId}`

Updates an existing notice. Only managers should call this endpoint.

**Auth required:** Yes (Manager)

**Path parameters:**

| Parameter  | Type   | Description |
|-----------|--------|-------------|
| `noticeId` | `Long` | Notice ID   |

**Request body:** Same fields as [POST `/api/notices`](#post-apinotices) (all fields optional in update context).

**Response `200 OK`:** Updated [Notice](#notice) object.

**Response `404 Not Found`:** Notice does not exist.

---

### DELETE `/api/notices/{noticeId}`

Deletes a notice. Only managers should call this endpoint.

**Auth required:** Yes (Manager)

**Path parameters:**

| Parameter  | Type   | Description |
|-----------|--------|-------------|
| `noticeId` | `Long` | Notice ID   |

**Response `204 No Content`:** Successfully deleted.

**Response `404 Not Found`:** Notice does not exist.

---

## 3. Cooperative Material Multipliers

Material multipliers determine the XP weight applied to a specific material within a cooperative for the leaderboard calculation.

### POST `/api/cooperative-material-multipliers`

Creates or updates the multiplier for a cooperative-material combination (upsert).

**Auth required:** Yes

**Request body:**

```json
{
  "cooperativeId": 1,
  "materialId": 3,
  "multiplierValue": 1.25
}
```

| Field             | Type     | Required | Description                          |
|------------------|----------|----------|--------------------------------------|
| `cooperativeId`   | `Long`   | Yes      | Cooperative ID                       |
| `materialId`      | `Long`   | Yes      | Material ID                          |
| `multiplierValue` | `Double` | Yes      | Multiplier applied to material weight |

**Response `201 Created`:** [CooperativeMaterialMultiplier](#cooperativematerialmultiplier) object.

---

### GET `/api/cooperative-material-multipliers/cooperative/{cooperativeId}/material/{materialId}`

Retrieves the multiplier for a specific cooperative-material pair.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |
| `materialId`    | `Long` | Material ID    |

**Response `200 OK`:** [CooperativeMaterialMultiplier](#cooperativematerialmultiplier) object.

**Response `404 Not Found`:** No multiplier configured for this pair.

---

## 4. Cooperative Analytics

### GET `/api/cooperative/analytics/{cooperativeId}/performance`

Returns a high-level performance overview for the cooperative.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |

**Response `200 OK`:**

```json
{
  "totalCollected": 12500.5,
  "totalSold": 9800.0,
  "currentStock": 2700.5,
  "activeWorkers": 23
}
```

| Field            | Type      | Description                   |
|-----------------|-----------|-------------------------------|
| `totalCollected` | `Double`  | Total kg collected (all time) |
| `totalSold`      | `Double`  | Total kg sold (all time)      |
| `currentStock`   | `Double`  | Current stock in kg           |
| `activeWorkers`  | `Integer` | Number of active workers      |

---

### GET `/api/cooperative/analytics/{cooperativeId}/workers/productivity`

Returns productivity data for **all** workers in a cooperative within an optional date range.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |

**Query parameters:**

| Parameter   | Type            | Required | Default          | Description             |
|------------|-----------------|----------|-----------------|-------------------------|
| `startDate` | ISO-8601 DateTime | No     | Now − 1 month    | Range start (inclusive) |
| `endDate`   | ISO-8601 DateTime | No     | Now              | Range end (inclusive)   |

**Example:**

```
GET /api/cooperative/analytics/5/workers/productivity?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
```

**Response `200 OK`:**

```json
[
  {
    "workerId": 12,
    "workerName": "Maria Santos",
    "totalCollectedKg": 340.5,
    "numberOfWeighings": 18,
    "avgWeightPerWeighing": 18.92
  }
]
```

---

### GET `/api/cooperative/analytics/{cooperativeId}/workers/{workerId}/productivity`

Returns productivity data for a **specific** worker within an optional date range.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |
| `workerId`      | `Long` | Worker ID      |

**Query parameters:** Same as above (`startDate`, `endDate`).

**Response `200 OK`:** Array containing one [WorkerProductivityDTO](#workerproductivitydto) object.

---

### GET `/api/cooperative/analytics/{cooperativeId}/stock`

Returns current stock information broken down by material.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |

**Response `200 OK`:**

```json
[
  {
    "materialName": "Aluminium",
    "totalCollected": 5000.0,
    "totalSold": 4200.0,
    "currentStock": 800.0
  }
]
```

---

### GET `/api/cooperative/analytics/{cooperativeId}/revenue`

Returns revenue and sales data within an optional date range.

**Auth required:** Yes

**Path parameters:**

| Parameter       | Type   | Description    |
|----------------|--------|----------------|
| `cooperativeId` | `Long` | Cooperative ID |

**Query parameters:**

| Parameter   | Type              | Required | Default           | Description           |
|------------|-------------------|----------|------------------|-----------------------|
| `startDate` | ISO-8601 DateTime | No       | Now − 12 months  | Range start           |
| `endDate`   | ISO-8601 DateTime | No       | Now              | Range end             |

**Response `200 OK`:**

```json
[
  {
    "totalRevenue": 45200.75,
    "totalSales": 312,
    "avgPricePerKg": 4.82
  }
]
```

| Field           | Type     | Description                              |
|----------------|----------|------------------------------------------|
| `totalRevenue`  | `Double` | Total revenue (weight × price_per_kg)    |
| `totalSales`    | `Long`   | Number of individual sale transactions   |
| `avgPricePerKg` | `Double` | Average price per kilogram               |

---

## 5. Leaderboard

### GET `/api/getTop3`

Returns the top 3 workers in a cooperative ranked by XP score. XP is calculated as:

```
XP = weight × material_multiplier × weekly_random_multiplier
```

**Auth required:** Yes

**Query parameters:**

| Parameter       | Type   | Required | Description    |
|----------------|--------|----------|----------------|
| `cooperativeId` | `Long` | Yes      | Cooperative ID |

**Example:**

```
GET /api/getTop3?cooperativeId=1
```

**Response `200 OK`:**

```json
[
  {
    "worker_id": 7,
    "worker_name": "João Silva",
    "raw_xp": 520.5,
    "xp": 624.6
  },
  {
    "worker_id": 3,
    "worker_name": "Ana Pereira",
    "raw_xp": 480.0,
    "xp": 576.0
  },
  {
    "worker_id": 15,
    "worker_name": "Carlos Mendes",
    "raw_xp": 410.25,
    "xp": 492.3
  }
]
```

| Field         | Type     | Description                                  |
|--------------|----------|----------------------------------------------|
| `worker_id`   | `Long`   | Worker ID                                    |
| `worker_name` | `String` | Worker full name                             |
| `raw_xp`      | `Double` | XP before weekly random multiplier           |
| `xp`          | `Double` | Final XP (raw_xp × weekly random multiplier) |

**Notes:**
- The weekly random multiplier (0.8–1.5) is updated every **Thursday at 13:04:10** by a scheduled task.

---

## 6. Sales History

### GET `/getLast5Sales`

Returns the 5 most recent sales transactions for a specific material, ordered by date descending.

**Auth required:** Yes

**Query parameters:**

| Parameter    | Type   | Required | Description |
|-------------|--------|----------|-------------|
| `materialId` | `Long` | Yes      | Material ID |

**Example:**

```
GET /getLast5Sales?materialId=3
```

**Response `200 OK`:**

```json
[
  {
    "material": "Aluminium",
    "weight": 125.5,
    "price_kg": 4.80,
    "date": "2025-02-15T14:30:00"
  }
]
```

| Field      | Type       | Description                      |
|-----------|------------|----------------------------------|
| `material` | `String`   | Material name                    |
| `weight`   | `Double`   | Weight sold in kg                |
| `price_kg` | `Double`   | Price per kg at time of sale     |
| `date`     | `DateTime` | Date and time of the transaction |

---

## 7. Data Models

### Notice

```json
{
  "noticeId": 1,
  "title": "string",
  "content": "string",
  "priority": 1,
  "createdBy": 42,
  "createdAt": "2025-02-20T10:00:00Z",
  "lastUpdated": "2025-02-20T10:00:00Z",
  "expiresAt": "2025-03-01T00:00:00Z",
  "cooperativeId": null
}
```

| Field           | Type      | Description                                          |
|----------------|-----------|------------------------------------------------------|
| `noticeId`      | `Long`    | Primary key                                          |
| `title`         | `String`  | Notice title                                         |
| `content`       | `String`  | Full notice text                                     |
| `priority`      | `int`     | `1` = low, `2` = medium, `3` = high                 |
| `createdBy`     | `Long`    | Worker ID of the creator                             |
| `createdAt`     | `Instant` | Creation timestamp (UTC)                             |
| `lastUpdated`   | `Instant` | Last modification timestamp (UTC)                   |
| `expiresAt`     | `Instant` | Expiry timestamp — notice hidden after this (UTC)    |
| `cooperativeId` | `Long`    | Cooperative this notice belongs to; `null` = global |

---

### CooperativeMaterialMultiplier

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "cooperativeId": 1,
  "materialId": 3,
  "multiplierValue": 1.25,
  "lastUpdated": "2025-02-01T08:00:00"
}
```

| Field             | Type            | Description                      |
|------------------|-----------------|----------------------------------|
| `id`              | `UUID`          | Primary key                      |
| `cooperativeId`   | `Long`          | Cooperative ID                   |
| `materialId`      | `Long`          | Material ID                      |
| `multiplierValue` | `Double`        | XP multiplier for the material   |
| `lastUpdated`     | `LocalDateTime` | Auto-updated by the database     |

---

### WorkerProductivityDTO

```json
{
  "workerId": 12,
  "workerName": "Maria Santos",
  "totalCollectedKg": 340.5,
  "numberOfWeighings": 18,
  "avgWeightPerWeighing": 18.92
}
```

---

### CooperativePerformanceDTO

```json
{
  "totalCollected": 12500.5,
  "totalSold": 9800.0,
  "currentStock": 2700.5,
  "activeWorkers": 23
}
```

---

### StockByMaterialDTO

```json
{
  "materialName": "Aluminium",
  "totalCollected": 5000.0,
  "totalSold": 4200.0,
  "currentStock": 800.0
}
```

---

### RevenueDTO

```json
{
  "totalRevenue": 45200.75,
  "totalSales": 312,
  "avgPricePerKg": 4.82
}
```

---

## 8. Error Responses

| HTTP Status | When it occurs                                               |
|------------|--------------------------------------------------------------|
| `400`      | Invalid request body or failed validation (e.g. blank field, priority out of range, past expiry date) |
| `401`      | Missing or invalid JWT token                                 |
| `403`      | Authenticated but insufficient role (e.g. non-manager trying to create a notice) |
| `404`      | Requested resource not found                                 |
| `500`      | Unexpected server error                                      |

**Validation error example (`400`):**

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/api/notices"
}
```

---

## Quick Reference — All Endpoints

| Method   | Path                                                                              | Auth | Description                            |
|---------|-----------------------------------------------------------------------------------|------|----------------------------------------|
| `POST`  | `/api/auth/login`                                                                 | No   | Login and receive JWT token            |
| `GET`   | `/api/notices/all`                                                                | Yes  | All active global notices              |
| `GET`   | `/api/notices/cooperative/{cooperativeId}`                                        | Yes  | Notices for cooperative + global       |
| `GET`   | `/api/notices/{noticeId}`                                                         | Yes  | Single notice by ID                    |
| `GET`   | `/api/notices/filter/{priority}`                                                  | Yes  | Notices filtered by priority (1–3)     |
| `POST`  | `/api/notices`                                                                    | Yes  | Create notice (Manager)                |
| `PUT`   | `/api/notices/{noticeId}`                                                         | Yes  | Update notice (Manager)                |
| `DELETE`| `/api/notices/{noticeId}`                                                         | Yes  | Delete notice (Manager)                |
| `POST`  | `/api/cooperative-material-multipliers`                                           | Yes  | Create/update material multiplier      |
| `GET`   | `/api/cooperative-material-multipliers/cooperative/{cId}/material/{mId}`         | Yes  | Get specific material multiplier       |
| `GET`   | `/api/cooperative/analytics/{cooperativeId}/performance`                          | Yes  | Cooperative performance overview       |
| `GET`   | `/api/cooperative/analytics/{cooperativeId}/workers/productivity`                 | Yes  | All workers productivity               |
| `GET`   | `/api/cooperative/analytics/{cooperativeId}/workers/{workerId}/productivity`      | Yes  | Single worker productivity             |
| `GET`   | `/api/cooperative/analytics/{cooperativeId}/stock`                                | Yes  | Stock by material                      |
| `GET`   | `/api/cooperative/analytics/{cooperativeId}/revenue`                              | Yes  | Revenue and sales data                 |
| `GET`   | `/api/getTop3?cooperativeId={id}`                                                 | Yes  | Top 3 workers leaderboard              |
| `GET`   | `/getLast5Sales?materialId={id}`                                                  | Yes  | Last 5 sales for a material            |
