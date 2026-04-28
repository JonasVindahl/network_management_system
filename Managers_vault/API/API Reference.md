# API Reference

All `/api/**` endpoints require a valid JWT except `/api/auth/**`. Swagger UI and OpenAPI docs are public through the configured security rules.

Use `Authorization: Bearer <token>` unless the browser flow stores the JWT cookie.

## Authentication

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Public | Authenticate a worker by CPF/password and return a JWT. |

Body:

- `cpf`
- `password`

## Cooperative and Buyer Lookup

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/cooperatives` | Manager/Admin | List cooperative IDs and names ordered by name. |
| GET | `/api/buyers` | Manager/Admin | List buyer IDs and names ordered by name. |

## Analytics

| Method | Path | Access | Query | Purpose |
| --- | --- | --- | --- | --- |
| GET | `/api/performance` | Manager/Admin | `cooperativeId`, `startDate`, `endDate` | Return total collected, sold, current stock, and active worker count. Workers are forbidden. |
| GET | `/api/productivity` | Worker/Manager/Admin | `cooperativeId`, `workerId`, `startDate`, `endDate` | Return worker productivity. Workers are forced to their own worker ID. |
| GET | `/api/revenue` | Manager/Admin | `cooperativeId`, `materialId`, `startDate`, `endDate` | Return revenue, sale count, average price per kg, and material. Workers are forbidden. |
| GET | `/api/cooperative/materials` | Manager/Admin | none | Return materials that have sold sales, ordered by material name. Workers are forbidden. |
| GET | `/api/cooperative/lastsales/all` | Manager/Admin | `materialId` | Return last five sold normal sales for a material across all cooperatives. |
| GET | `/api/cooperative/lastsales` | Manager/Admin | `cooperativeId`, `materialId` | Return last five sold normal sales for a material in one cooperative. |
| GET | `/api/stock` | Manager/Admin | `cooperativeId` | Return stock by material for a cooperative. |
| GET | `/api/getLast5Sales` | Manager/Admin | `materialId` | Return raw last-five sale rows across all cooperatives. |

Date query parameters use ISO date-time parsing where annotated with `@DateTimeFormat`.

## Materials and Stock

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| POST | `/api/insertMaterial` | Admin | Record a material weighing, update bag state, and increment stock by the computed delta. |
| POST | `/api/stock` | Manager/Admin | Manually add stock for the authenticated cooperative. |

`POST /api/insertMaterial` body:

```json
{
  "materialId": 3,
  "workerId": 7,
  "amount": 12.5,
  "bagFull": false,
  "deviceId": 1
}
```

Validation observed in controller:

- `materialId` must be nonzero.
- `workerId` must be nonzero.
- `amount` must be greater than zero.
- `deviceId` must be nonzero.

`POST /api/stock` body:

```json
{
  "materialId": 3,
  "amount": 50.0
}
```

## Normal Sales

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/sales/history` | Manager/Admin | Combined normal and collective sale history for a cooperative and date range. |
| GET | `/api/sales/active` | Manager/Admin | Combined active normal and collective sales for a cooperative. |
| GET | `/api/sales` | Manager/Admin | Normal sales by status: `ACTIVE` or `HISTORY`. |
| POST | `/api/sales` | Manager/Admin | Create a normal sale. |
| PUT | `/api/sales/{saleId}` | Manager/Admin | Update an active normal sale. |
| PATCH | `/api/sales/{saleId}/complete` | Manager/Admin | Complete a normal sale and subtract stock. |
| PATCH | `/api/sales/{saleId}/cancel` | Manager/Admin | Cancel an active normal sale. |

History query parameters:

- `cooperativeId`
- `startDate`
- `endDate`
- `type`: `REGULAR`, `COLLECTIVE`, or `ALL`; default `ALL`

Active query parameters:

- `cooperativeId`
- `type`: `REGULAR`, `COLLECTIVE`, or `ALL`; default `ALL`

Normal sales query parameters:

- `cooperativeId`
- `status`: `ACTIVE` or `HISTORY`; default `ACTIVE`

Create body:

```json
{
  "materialId": 1,
  "weight": 100.0,
  "priceKg": 2.5,
  "buyerId": 4,
  "expectedSaleDate": "2026-05-01T10:00:00Z"
}
```

Update body supports the same fields as nullable partial updates.

## Collective Sales

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/collective-sale` | Manager/Admin | List active collective sales. Admin sees all active; managers see relevant sales for their cooperative. |
| GET | `/api/collective-sale/invitations` | Manager/Admin | List pending invitations for the authenticated cooperative. |
| POST | `/api/collective-sale` | Manager/Admin | Create a collective sale and add creator contribution as `ACCEPTED`. |
| POST | `/api/collective-sale/{saleId}/invite` | Manager/Admin | Invite another cooperative. |
| POST | `/api/collective-sale/{saleId}/join` | Manager/Admin | Accept an invitation. |
| PUT | `/api/collective-sale/{saleId}/contribution` | Manager/Admin | Set contribution weight and reserve or release stock. |
| PUT | `/api/collective-sale/{saleId}/material` | Manager/Admin | Change material, only while no accepted contribution has reserved stock. |
| PUT | `/api/collective-sale/{saleId}/price` | Manager/Admin | Change price per kg. |
| DELETE | `/api/collective-sale/{saleId}/leave` | Manager/Admin | Leave a collective sale and return reserved stock. Creator cannot leave. |
| GET | `/api/collective-sale/my` | Manager/Admin | List own cooperative's active or history collective sales. |
| DELETE | `/api/collective-sale/{saleId}` | Manager/Admin | Cancel a collective sale and return all reserved stock. |

Create body:

```json
{
  "materialId": 1,
  "buyerId": 4,
  "pricePerKg": 2.5,
  "expectedSaleDate": "2026-05-01T10:00:00Z"
}
```

Invite body:

```json
{
  "cooperativeId": 2
}
```

Contribution body:

```json
{
  "weight": 500.0
}
```

Material update body:

```json
{
  "materialId": 3
}
```

Price update body:

```json
{
  "pricePerKg": 3.75
}
```

`GET /api/collective-sale/my` query:

- `status`: `ACTIVE` or `HISTORY`; default `ACTIVE`

## Reports and PDFs

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/reports/sales/normal/{saleId}` | Manager/Admin | Return JSON normal sale report. |
| GET | `/api/reports/sales/collective/{saleId}` | Manager/Admin | Return JSON collective sale report. |
| GET | `/api/reports/pdf/normal-sale/{saleId}` | Manager/Admin | Download a normal sale report PDF. |
| GET | `/api/reports/pdf/collective-sale/{saleId}` | Manager/Admin | Download a collective sale report PDF. |

Optional query:

- `cooperativeId`

Managers can only access reports for their own cooperative. Admins can omit cooperative ID for report reads where service access permits it.

## Notices

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/notices` | Authenticated | List active global and cooperative notices. Admin must provide `cooperativeId`. |
| GET | `/api/notices/global` | Admin | List active global notices. |
| GET | `/api/notices/{noticeId}` | Authenticated | Read a notice if global, own cooperative, or admin. |
| GET | `/api/notices/filter` | Authenticated | Filter active notices by priority and cooperative scope. |
| POST | `/api/notices` | Manager/Admin | Create a notice. |
| PUT | `/api/notices/{noticeId}` | Manager/Admin | Update a notice. |
| DELETE | `/api/notices/{noticeId}` | Manager/Admin | Delete a notice. |

Create/update body:

```json
{
  "title": "Notice title",
  "content": "Notice content",
  "priority": 2,
  "expiresAt": "2026-05-01T10:00:00Z",
  "cooperativeId": 1
}
```

Managers always write notices for their own cooperative. Admins may create global notices by leaving `cooperativeId` null.

## Multipliers

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| POST | `/api/multipliers` | Manager/Admin | Save or update a cooperative material multiplier. |
| GET | `/api/multipliers` | Manager/Admin | List all material multipliers for a cooperative, defaulting missing values to `1.0`. |
| GET | `/api/multipliers/single` | Manager/Admin | Fetch one multiplier by cooperative and material. |

Body:

```json
{
  "cooperativeId": 1,
  "materialId": 3,
  "multiplierValue": 1.25
}
```

Query parameters:

- `cooperativeId`
- `materialId` for `/single`

## Gamification

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/achievements` | Authenticated | List achievements with cooperative-specific effective XP. |
| PATCH | `/api/achievements/{achievementId}/xp` | Manager/Admin | Override XP reward for one achievement in a cooperative. |
| GET | `/api/achievements/workers/{workerId}/month` | Authenticated | Return a worker's monthly achievement summary. Workers are forced to self. |
| GET | `/api/achievements/workers/{workerId}/top-month` | Authenticated | Return the worker's best month in the current year. |
| GET | `/api/achievements/workers/{workerId}/top-day` | Authenticated | Return the worker's best day in a month. |
| GET | `/api/levels` | Authenticated | List all level definitions. |
| GET | `/api/levels/worker/{workerId}` | Authenticated | Return a worker's level. Workers are forced to self. |
| GET | `/api/leaderboard` | Authenticated | Return current leaderboard snapshot for a cooperative. |
| GET | `/api/leaderboard/history` | Authenticated | Return a specific leaderboard snapshot. |

Achievement XP override body:

```json
{
  "xpReward": 250
}
```

Common query parameters:

- `cooperativeId`
- `yearMonth` as `YYYY-MM`
- `weekNumber` from `1` to `4`

## Browser Routes

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/` | Redirects to `/login`. |
| GET | `/login` | Login page. |
| GET | `/frontend` | Manager dashboard page. |
| GET | `/normal-sale` | Normal sale page. |
| GET | `/collective-sale` | Collective sale page. |

## Related Notes

- [[Authentication and Roles]]
- [[Architecture/Frontend Views|Frontend Views]]
- [[Domain/Normal Sales]]
- [[Domain/Collective Sales]]
- [[Domain/Gamification]]

