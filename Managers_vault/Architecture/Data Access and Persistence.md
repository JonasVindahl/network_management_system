# Data Access and Persistence

## Persistence Style

The codebase uses a mix of:

- Spring Data JPA repositories with entity classes.
- Native SQL queries through `@Query(nativeQuery = true)`.
- Direct `JdbcTemplate` queries and updates.

Most complex reads and writes are native SQL. Entities exist mainly where Spring Data repository support is useful.

## Database

The schema in `Database/DMS_db_schema.sql` creates PostgreSQL tables for:

- cooperatives, workers, groups, materials, buyers, devices
- notices
- normal sales and collective sales
- measurements, stock, worker contributions, bag state
- material multipliers and random cooperative multipliers
- achievement definitions, XP overrides, worker achievements
- leaderboard snapshots and entries
- level definitions and worker levels

See [[Models/Database Schema]] for table-level documentation.

## JPA Entities

Observed entity classes:

- `CooperativeEntity` -> `public.cooperative`
- `BuyerEntity` -> `public.buyers`
- `CollectiveSaleEntity` -> `collective_sale`
- `Notice` -> `notice_board`
- `Measurement` -> `public.measurements`
- `Stock` -> `public.stock`
- `MaterialBagState` -> `public.material_bag_state`
- `CooperativeMaterialMultiplier` -> `cooperative_material_multiplier`

Several repositories extend `JpaRepository<CooperativeEntity, Long>` even when they return custom report or analytics rows. Those repositories use native SQL projections instead of loading cooperative entities.

## Repository Patterns

### Object array projections

Many repository methods return `List<Object[]>` and services map positional columns into DTOs. Examples:

- `SalesRepository`
- `CollectiveSaleRepository`
- `ReportsRepository`
- `SaleReportsRepository`
- `AnalyticsRepository`
- `BuyerRepository`
- `CooperativeRepository`

### JdbcTemplate services

Gamification and authentication use `JdbcTemplate` directly:

- `WorkerDetailsService`
- `AchievementService`
- `LevelService`
- `LeaderboardService`
- `MonthlyRandomMultiplier`
- `LeaderboardScheduler`
- `AchievementEvaluationScheduler`
- `Last5SalesController`

## Transaction Boundaries

Observed transactional methods:

- `MaterialService.insertMaterial`
- `StockController.addStock`
- normal sale create, update, complete, and cancel operations in `SalesService`
- collective sale create, invite, join, leave, contribution update, material update, price update, and cancel operations in `CollectiveSaleService`
- multiplier save/update in `CooperativeMaterialMultiplierService`
- modifying repository methods in several repositories

## Stock Accounting Rules

`StockRepository` is the central stock writer:

- `addToStock`: increases `total_collected_kg` and `current_stock_kg`.
- `addToStockDecimal`: same as `addToStock` with `BigDecimal`.
- `insertStockRow`: creates a new stock row when manual stock is added to a cooperative/material pair for the first time.
- `recordSale`: increases `total_sold_kg` and decreases `current_stock_kg` only when enough stock exists.
- `adjustStock`: subtracts a reservation delta from `current_stock_kg`; negative delta returns stock.
- `findCurrentStock`: reads current stock for conflict messages.

## Schema Validation

`spring.jpa.hibernate.ddl-auto=validate` means Hibernate checks that mappings align with the existing schema rather than creating or updating tables automatically.

## Related Notes

- [[Models/Database Schema]]
- [[Models/Java Models and DTOs]]
- [[Domain/Materials Stock and Measurements]]
- [[Domain/Normal Sales]]
- [[Domain/Collective Sales]]

