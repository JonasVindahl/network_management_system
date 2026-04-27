# Scheduled Jobs

The application enables scheduling through `@EnableScheduling` on scheduler components.

## Monthly Random Multiplier

Class: `MonthlyRandomMultiplier`

Schedule:

- cron: `0 0 0 1 * *`
- runs at midnight on the first day of each month

Behavior:

1. Reads all `cooperative_id` values from `cooperative`.
2. Generates a random multiplier between `0.8` and `1.5`.
3. Upserts one row per cooperative into `cooperative_random_multiplier`.
4. Updates `last_updated` to `NOW()`.

Used by:

- leaderboard XP calculation in `LeaderboardService.computeAndPersistSnapshot`.

## Achievement Evaluation Scheduler

Class: `gamification.levels.AchievementEvaluationScheduler`

Schedule:

- cron: `0 0 2 * * *`
- runs daily at 02:00

Behavior:

1. Builds the current `YYYY-MM`.
2. Reads all cooperatives.
3. Calls `AchievementService.evaluateAchievementsForCooperative` for each cooperative.
4. Reads active workers in that cooperative.
5. Calls `LevelService.recalculateWorkerLevel` for each active worker.

Effects:

- Updates `worker_achievement.progress_value`.
- Sets `worker_achievement.unlocked_at` the first time a threshold is reached.
- Updates `worker_level.total_xp`, `current_level`, and `last_updated`.

## Leaderboard Scheduler

Class: `gamification.leaderboard.LeaderboardScheduler`

Schedules:

- `0 0 3 7,14,21,28 * *`: runs at 03:00 on days 7, 14, 21, and 28.
- `0 0 3 1 * *`: runs at 03:00 on the first day of the month for the previous month's final week-four snapshot.

Behavior:

1. Determines the finished week block.
2. Reads all cooperatives.
3. Calls `LeaderboardService.computeAndPersistSnapshot` per cooperative.
4. Stores or updates one `leaderboard_snapshot`.
5. Deletes previous entries for that snapshot.
6. Inserts the top three `leaderboard_entry` rows.

Week ranges:

- week 1: days 1-7
- week 2: days 8-14
- week 3: days 15-21
- week 4: day 22 through the end of the month

Leaderboard XP formula observed in code:

```text
raw_xp = sum(weight_kg * material_multiplier) + achievement_xp
final_xp = raw_xp * cooperative_random_multiplier
```

## Related Notes

- [[Domain/Gamification]]
- [[Models/Database Schema]]
- [[Operations/Runbook]]

