# Database Schema

Source: `Database/DMS_db_schema.sql`

The schema targets PostgreSQL. Table and column names are lower-case to avoid quoted identifier issues with Hibernate/JPA.

## Core Tables

### `cooperative`

Stores cooperative identity and contact data.

Columns:

- `cooperative_id` primary key
- `cooperative_name`
- `cooperative_location`
- `contact_email`
- `phone_number`
- `created_at`
- `last_updated`

Referenced by workers, devices, notices, collective sales, stock, contributions, multipliers, achievements, and leaderboards.

### `groups`

Stores material groups.

Columns:

- `group_id` primary key
- `group_name`

### `materials`

Stores material definitions.

Columns:

- `material_id` primary key
- `material_name`
- `material_group` foreign key to `groups.group_id`

### `buyers`

Stores sale buyers.

Columns:

- `buyer_id` primary key
- `buyer_name`

### `workers`

Stores workers and login data.

Columns:

- `worker_id` primary key
- `worker_name`
- `cooperative` foreign key to `cooperative.cooperative_id`
- `cpf` as `bytea`
- `user_type` as `char`
- `birth_date`
- `enter_date`
- `exit_date`
- `pis` as `bytea`
- `rg` as `bytea`
- `gender`
- `password` as `bytea`
- `email`
- `last_update`

Role values observed in code:

- `A`: admin
- `M`: manager
- `W`: worker

### `devices`

Stores weighing devices.

Columns:

- `device_id` primary key
- `cooperative_id` foreign key to `cooperative.cooperative_id`

## Notice Tables

### `notice_board`

Stores global or cooperative notices.

Columns:

- `notice_id` primary key
- `cooperative_id`, nullable; null means global
- `created_at`
- `last_updated`
- `created_by` foreign key to `workers.worker_id`
- `priority`
- `expires_at`
- `title`
- `content`

## Sales Tables

### `sales`

Stores normal sales.

Columns:

- `sale_id` primary key
- `created_at`
- `sold_at`
- `cancelled_at`
- `material` foreign key to `materials.material_id`
- `weight`
- `price_kg`
- `buyer` foreign key to `buyers.buyer_id`
- `responsible` foreign key to `workers.worker_id`
- `cooperative_id` foreign key to `cooperative.cooperative_id`
- `expected_sale_date`

### `collective_sale`

Stores collective sales.

Columns:

- `collective_sale_id` primary key
- `created_at`
- `sold_at`
- `cancelled_at`
- `buyer_id` foreign key to `buyers.buyer_id`
- `material_id` foreign key to `materials.material_id`
- `total_weight`
- `price_kg`
- `expected_sale_date`
- `creator_cooperative_id` foreign key to `cooperative.cooperative_id`

### `collective_sale_contribution`

Stores cooperative participation in a collective sale.

Columns:

- `contribution_id` primary key
- `collective_sale_id` foreign key to `collective_sale.collective_sale_id`
- `cooperative_id` foreign key to `cooperative.cooperative_id`
- `contributed_weight`
- `revenue_share`
- `status`

Status values documented in schema:

- `INVITED`
- `ACCEPTED`
- `LEFT`

Constraint:

- unique pair `(collective_sale_id, cooperative_id)`

## Measurement and Stock Tables

### `measurements`

Stores individual weighing events.

Columns:

- `weighting_id` primary key
- `weight_kg`
- `time_stamp`
- `wastepicker` foreign key to `workers.worker_id`
- `material` foreign key to `materials.material_id`
- `device` foreign key to `devices.device_id`
- `bag_filled`

### `stock`

Tracks collected, sold, and available stock per cooperative/material.

Columns:

- `stock_id` primary key
- `cooperative` foreign key to `cooperative.cooperative_id`
- `material` foreign key to `materials.material_id`
- `total_collected_kg`
- `total_sold_kg`
- `current_stock_kg`

Constraint:

- unique pair `(cooperative, material)`

### `worker_contributions`

Tracks worker material contributions over a date range.

Columns:

- `contribution_id` primary key
- `wastepicker` foreign key to `workers.worker_id`
- `material` foreign key to `materials.material_id`
- `cooperative` foreign key to `cooperative.cooperative_id`
- `period` as `daterange`
- `weight_kg`
- `last_updated`

### `material_bag_state`

Tracks current bag state per cooperative/material.

Columns:

- `bag_state_id` primary key
- `cooperative_id`
- `material_id`
- `is_begun`
- `current_kg`
- `last_updated`

Constraints:

- unique pair `(cooperative_id, material_id)`
- `current_kg >= 0`

## Multiplier Tables

### `cooperative_material_multiplier`

Stores cooperative-specific material multipliers.

Columns:

- `cooperative_material_multiplier_id` UUID primary key
- `cooperative_id`
- `material_id`
- `multiplier_value`
- `last_updated`

### `cooperative_random_multiplier`

Stores the monthly random multiplier for each cooperative.

Columns:

- `cooperative_random_multiplier_id` UUID primary key
- `cooperative_id`
- `multiplier_value`
- `last_updated`

Constraint:

- unique `cooperative_id`

## Gamification Tables

### `achievement_definition`

Stores fixed achievement definitions.

Columns:

- `achievement_id` primary key
- `achievement_key`
- `achievement_name`
- `description`
- `category`
- `threshold_value`
- `base_xp_reward`
- `difficulty`

Seeded categories:

- `WEIGHT`
- `DAYS_WORKED`
- `ACHIEVEMENTS_COUNT`

### `achievement_xp_override`

Stores cooperative-specific XP overrides.

Columns:

- `override_id` primary key
- `cooperative_id`
- `achievement_id`
- `xp_reward_override`
- `updated_by`
- `updated_at`

Constraint:

- unique pair `(cooperative_id, achievement_id)`

### `worker_achievement`

Stores monthly worker achievement progress.

Columns:

- `worker_achievement_id` primary key
- `worker_id`
- `achievement_id`
- `cooperative_id`
- `year_month` as `YYYY-MM`
- `unlocked_at`
- `progress_value`

Constraint:

- unique tuple `(worker_id, achievement_id, cooperative_id, year_month)`

### `leaderboard_snapshot`

Stores precomputed leaderboard snapshots.

Columns:

- `snapshot_id` primary key
- `cooperative_id`
- `year_month`
- `week_number`
- `computed_at`

Constraint:

- unique tuple `(cooperative_id, year_month, week_number)`

### `leaderboard_entry`

Stores leaderboard entries for a snapshot.

Columns:

- `entry_id` primary key
- `snapshot_id`
- `rank_position`
- `worker_id`
- `worker_name`
- `raw_xp`
- `final_xp`
- `random_mult`

Deleting a snapshot cascades to entries.

### `level_definition`

Stores fixed level definitions.

Columns:

- `level_number` primary key
- `level_name`
- `xp_required`

Seeded levels:

- 1 Beginner, 100 XP
- 2 Amateur, 167 XP
- 3 Apprentice, 278 XP
- 4 Collector, 464 XP
- 5 Professional, 774 XP
- 6 Expert, 1291 XP
- 7 Master, 2154 XP
- 8 Elite, 3593 XP
- 9 Champion, 5992 XP
- 10 Legend, 10000 XP

### `worker_level`

Stores global worker XP and level.

Columns:

- `worker_id` primary key and foreign key to `workers.worker_id`
- `total_xp`
- `current_level` foreign key to `level_definition.level_number`
- `last_updated`

## Related Notes

- [[Data Access and Persistence]]
- [[Java Models and DTOs]]
- [[Domain/Gamification]]

