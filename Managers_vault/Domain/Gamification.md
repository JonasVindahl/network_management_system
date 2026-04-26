# Gamification

## Purpose

Gamification tracks worker achievements, levels, material multipliers, random cooperative multipliers, and leaderboard snapshots.

## Main Files

Achievements:

- `AchievementController`
- `AchievementService`
- `AchievementDTO`
- `UpdateAchievementXPDTO`
- `WorkerMonthSummaryDTO`
- `AchievementEvaluationScheduler`

Levels:

- `LevelController`
- `LevelService`
- `LevelDTO`

Leaderboards:

- `LeaderboardController`
- `LeaderboardService`
- `LeaderboardDTO`
- `LeaderboardScheduler`

Multipliers:

- `CooperativeMaterialMultiplierController`
- `CooperativeMaterialMultiplierService`
- `CooperativeMaterialMultiplierRepository`
- `CooperativeMaterialMultiplier`
- `MultiplierDTO`
- `MonthlyRandomMultiplier`

## Achievement Definitions

Definitions are seeded in `Database/DMS_db_schema.sql`.

Observed categories:

- `WEIGHT`
- `DAYS_WORKED`
- `ACHIEVEMENTS_COUNT`

Managers cannot create new definitions through the observed API. They can override XP values per cooperative.

## Achievement Reads

Endpoint:

- `GET /api/achievements`

Behavior:

- Lists definitions.
- Applies cooperative-specific XP overrides with `COALESCE(axo.xp_reward_override, ad.base_xp_reward)`.

## Achievement XP Override

Endpoint:

- `PATCH /api/achievements/{achievementId}/xp`

Access:

- manager or admin

Behavior:

1. Resolves target cooperative.
2. Verifies achievement exists.
3. Upserts `achievement_xp_override`.
4. Stores `updated_by` as authenticated worker ID.

## Worker Month Summary

Endpoint:

- `GET /api/achievements/workers/{workerId}/month`

Behavior:

- Workers are forced to their own worker ID.
- Default `yearMonth` is current month.
- Verifies the worker belongs to the cooperative.
- Computes total monthly weight and days worked.
- Reads each achievement with progress and unlock state.
- Computes total unlocked achievements and XP earned.

## Top Month and Top Day

Endpoints:

- `GET /api/achievements/workers/{workerId}/top-month`
- `GET /api/achievements/workers/{workerId}/top-day`

Top month:

- Looks at current-year unlocked achievement XP.
- Falls back to current month summary when no rows exist.

Top day:

- Uses measurement totals by date for a given month.
- Defaults to current month.

## Achievement Evaluation

Daily scheduler:

- `AchievementEvaluationScheduler.evaluateCurrentMonth`
- cron: `0 0 2 * * *`

Evaluation:

- `WEIGHT` progress equals monthly measured kilograms.
- `DAYS_WORKED` progress equals distinct measurement dates in the month.
- `ACHIEVEMENTS_COUNT` progress equals unlocked non-count achievements.

Unlock behavior:

- `unlocked_at` is set when progress reaches threshold.
- Existing `unlocked_at` is preserved after first unlock.

## Levels

Endpoints:

- `GET /api/levels`
- `GET /api/levels/worker/{workerId}`

`LevelService`:

- ensures `worker_level` exists for a worker.
- sums all unlocked achievement XP.
- applies cooperative-specific XP overrides.
- finds the highest level whose `xp_required <= totalXp`.
- updates `worker_level`.

## Leaderboards

Endpoints:

- `GET /api/leaderboard`
- `GET /api/leaderboard/history`

Current leaderboard display rule:

- days 1-7: previous month week 4
- days 8-14: current month week 1
- days 15-21: current month week 2
- days 22-28: current month week 3
- days 29-end: current month week 4

Snapshot formula:

```text
weight_xp = sum(measurement.weight_kg * cooperative_material_multiplier)
raw_xp = weight_xp + achievement_xp
final_xp = raw_xp * cooperative_random_multiplier
```

Only the top three workers are inserted into `leaderboard_entry`.

## Multipliers

Material multiplier endpoints:

- `POST /api/multipliers`
- `GET /api/multipliers`
- `GET /api/multipliers/single`

Material multipliers:

- stored per cooperative/material
- default to `1.0` in list queries when no row exists
- must be positive

Random multiplier:

- generated monthly by `MonthlyRandomMultiplier`
- range: `0.8` to `1.5`
- stored in `cooperative_random_multiplier`

## Related Notes

- [[Architecture/Scheduled Jobs|Scheduled Jobs]]
- [[API/API Reference|API Reference]]
- [[Models/Database Schema|Database Schema]]

