# Java Models and DTOs

This note lists the observed entity and data transfer classes. See [[Planning/Code Inventory]] for every source file.

## Entity Classes

### `CooperativeEntity`

Table: `public.cooperative`

Fields:

- `cooperativeId`
- `cooperativeName`
- `cooperativeLocation`
- `contactEmail`
- `phoneNumber`
- `createdAt`
- `lastUpdated`

### `BuyerEntity`

Table: `public.buyers`

Fields:

- `buyerId`
- `buyerName`

### `CollectiveSaleEntity`

Table: `collective_sale`

Fields:

- `collectiveSaleId`
- `materialId`
- `buyerId`
- `pricePerKg`
- `expectedSaleDate`
- `totalWeight`
- `creatorCooperativeId`
- `createdAt`
- `soldAt`
- `cancelledAt`

### `Notice`

Table: `notice_board`

Fields:

- `noticeId`
- `title`
- `content`
- `createdAt`
- `lastUpdated`
- `priority`
- `createdBy`
- `expiresAt`
- `cooperativeId`

Methods include `isExpired()`.

### `Measurement`

Table: `public.measurements`

Fields:

- `weightingId`
- `weightKg`
- `timeStamp`
- `wastepicker`
- `material`
- `device`
- `bagFilled`

### `Stock`

Table: `public.stock`

Fields:

- `stockId`
- `cooperative`
- `material`
- `totalCollectedKg`
- `totalSoldKg`
- `currentStockKg`

### `MaterialBagState`

Table: `public.material_bag_state`

Fields:

- `bagStateId`
- `cooperativeId`
- `materialId`
- `isBegun`
- `currentKg`
- `lastUpdated`

### `CooperativeMaterialMultiplier`

Table: `cooperative_material_multiplier`

Fields:

- `multiplierId`
- `cooperativeId`
- `materialId`
- `multiplierValue`
- `lastUpdated`

## Authentication Models

- `AuthRequest`: `cpf`, `password`.
- `AuthResponse`: `token`.
- `JwtPrincipal`: `cpf`, `role`, `cooperativeId`, `workerId`.
- `WorkerInfo`: `workerId`, `cooperativeId`, `role`.

## Sales DTOs

### Normal sales

- `CreateSaleDTO`: `materialId`, `weight`, `priceKg`, `buyerId`, `expectedSaleDate`.
- `UpdateSaleDTO`: nullable update fields for `weight`, `priceKg`, `materialId`, `buyerId`, `expectedSaleDate`.
- `SaleDTO`: `saleId`, `saleType`, timestamps, material name, weight, price, total revenue, buyer, status, collective sale ID, cooperative count.

### Collective sales

- `CreateCollectiveSaleDTO`: `materialId`, `buyerId`, `pricePerKg`, `expectedSaleDate`.
- `InviteCooperativeDTO`: `cooperativeId`.
- `UpdateContributionDTO`: `weight`.
- `UpdateSaleMaterialDTO`: `materialId`.
- `UpdateSalePriceDTO`: `pricePerKg`.
- `ActiveCollectiveSaleDTO`: sale identity, material, buyer, price, dates, creator, status, `myStatus`, sold/cancelled timestamps.
- `CollectiveSaleInvitationDTO`: sale identity, material, buyer, price, expected date, created date, creator cooperative.

## Report DTOs

- `SaleReportDTO`: normal sale report details including sale status, material, buyer, responsible worker, cooperative, timestamps, weight, price per kg, and total revenue.
- `CollectiveSaleReportDTO`: collective report details including sale status, material, buyer, timestamps, total weight, price per kg, total revenue, total cooperatives, creator cooperative, and contributions.
- `ContributionDetailDTO`: cooperative ID/name, contributed weight, percentage of total, revenue share, and status.

## Analytics DTOs

- `CooperativeDTO`: cooperative ID and name.
- `CooperativePerformanceDTO`: total collected, total sold, current stock, active worker count.
- `WorkerProductivityDTO`: worker ID/name, total collected kg, number of weighings, average weight per weighing.
- `RevenueDTO`: total revenue, total sales, average price per kg, material name, material ID.
- `StockByMaterialDTO`: material name, total collected, total sold, current stock, material ID.
- `Last5SalesDTO`: material, weight, price per kg, date.

## Buyer DTO

- `BuyerDTO`: buyer ID and name.

## Material and Stock DTOs

- `MaterialRequest`: material ID, worker ID, amount, `bagFull`, device ID.
- `AddStockDTO`: material ID and amount.

## Notice DTO

- `NoticeDTO`: title, content, priority, expiry timestamp, optional cooperative ID.

Validation annotations require nonblank title/content, priority between 1 and 3, and future expiry.

## Multiplier DTO

- `MultiplierDTO`: cooperative ID, material ID, material name, multiplier value.

## Gamification DTOs

- `AchievementDTO`: achievement identity, key, name, description, category, threshold, XP reward, difficulty, progress, unlocked flag, unlocked timestamp.
- `UpdateAchievementXPDTO`: achievement ID and XP reward.
- `WorkerMonthSummaryDTO`: worker identity, year-month, total weight, days worked, unlocked achievement count, total XP, achievement list.
- `LevelDTO`: level number/name, required XP, XP to next level, worker ID, total XP, current-level flag.
- `LeaderboardDTO`: year-month, week number, computed timestamp, entries.
- `LeaderboardDTO.LeaderboardEntryDTO`: rank, worker ID/name, raw XP, final XP, random multiplier.

## Related Notes

- [[Database Schema]]
- [[Planning/Code Inventory]]
- [[API/API Reference]]

