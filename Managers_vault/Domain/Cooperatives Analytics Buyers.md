# Cooperatives, Analytics, and Buyers

## Purpose

This area supports lookup data and dashboard analytics for cooperative management.

## Main Files

- `CooperativeController`
- `CooperativeService`
- `CooperativeRepository`
- `CooperativeEntity`
- `CooperativeDTO`
- `AnalyticsController`
- `AnalyticsService`
- `AnalyticsRepository`
- `BuyerController`
- `BuyerService`
- `BuyerRepository`
- `BuyerEntity`
- `BuyerDTO`
- `Last5SalesController`

## Cooperative Lookup

Endpoint:

- `GET /api/cooperatives`

Access:

- manager or admin

Behavior:

- Reads `cooperative_id` and `cooperative_name`.
- Orders by cooperative name.
- Returns `CooperativeDTO`.

## Buyer Lookup

Endpoint:

- `GET /api/buyers`

Access:

- manager or admin

Behavior:

- Reads `buyer_id` and `buyer_name`.
- Orders by buyer name.
- Returns `BuyerDTO`.

## Performance Analytics

Endpoint:

- `GET /api/performance`

Access:

- manager own cooperative
- admin with `cooperativeId`
- workers forbidden

Returns:

- total collected
- total sold
- current stock
- active worker count

Observed repository behavior:

- The SQL sums the `stock` table and counts active workers.
- The method accepts `startDate` and `endDate`, but the SQL does not use those parameters.

## Productivity Analytics

Endpoint:

- `GET /api/productivity`

Access:

- worker self only
- manager own cooperative
- admin with `cooperativeId`

Returns:

- worker ID
- worker name
- total collected kg
- number of weighings
- average weight per weighing

Default date behavior:

- `endDate`: now
- `startDate`: 1000 days before end date

## Revenue Analytics

Endpoint:

- `GET /api/revenue`

Access:

- manager own cooperative
- admin with `cooperativeId`
- workers forbidden

Returns by material:

- total revenue
- total sales
- average price per kg
- material name
- material ID

Optional filter:

- `materialId`

Default date behavior:

- `endDate`: now
- `startDate`: 1000 days before end date

## Material Lists and Last Sales

`GET /api/cooperative/materials`:

- Manager/admin only.
- Returns distinct materials that appear in sold normal sales.

`GET /api/cooperative/lastsales/all?materialId=...`:

- Manager/admin only.
- Returns last five sold normal sales for a material across all cooperatives.

`GET /api/cooperative/lastsales?cooperativeId=...&materialId=...`:

- Manager own cooperative or admin with cooperative ID.
- Returns last five sold normal sales for one cooperative/material.

`GET /api/getLast5Sales?materialId=...`:

- Blocks workers.
- Uses `JdbcTemplate` directly.
- Returns raw maps for last five sold normal sales across all cooperatives.

## Related Notes

- [[API/API Reference|API Reference]]
- [[Models/Database Schema|Database Schema]]
- [[Architecture/Data Access and Persistence|Data Access and Persistence]]

