package dk.aau.network_management_system.gamification.achievements;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@Service
public class AchievementService {

    private final JdbcTemplate jdbc;
    private final AuthenticatedUser authenticatedUser;
    private final PermissionHelper permissionHelper;

    public AchievementService(JdbcTemplate jdbc,
                               AuthenticatedUser authenticatedUser,
                               PermissionHelper permissionHelper) {
        this.jdbc = jdbc;
        this.authenticatedUser = authenticatedUser;
        this.permissionHelper = permissionHelper;
    }

    // PUBLIC API
    
    public List<AchievementDTO> listAllAchievements(Long cooperativeId) {
        return jdbc.query("""
            SELECT
                ad.achievement_id,
                ad.achievement_key,
                ad.achievement_name,
                ad.description,
                ad.category,
                ad.threshold_value,
                ad.difficulty,
                COALESCE(axo.xp_reward_override, ad.base_xp_reward) AS effective_xp
            FROM achievement_definition ad
            LEFT JOIN achievement_xp_override axo
                ON axo.achievement_id = ad.achievement_id
                AND axo.cooperative_id = ?
            ORDER BY ad.category, ad.threshold_value
            """, (rs, i) -> {
            AchievementDTO dto = new AchievementDTO();
            dto.setAchievementId(rs.getLong("achievement_id"));
            dto.setAchievementKey(rs.getString("achievement_key"));
            dto.setAchievementName(rs.getString("achievement_name"));
            dto.setDescription(rs.getString("description"));
            dto.setCategory(rs.getString("category"));
            dto.setThresholdValue(rs.getDouble("threshold_value"));
            dto.setDifficulty(rs.getString("difficulty"));
            dto.setXpReward(rs.getInt("effective_xp"));
            return dto;
        }, cooperativeId);
    }

    public void updateAchievementXp(Long cooperativeId, UpdateAchievementXPDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        permissionHelper.validateCooperativeOwnership(cooperativeId);

        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM achievement_definition WHERE achievement_id = ?",
            Integer.class, dto.getAchievementId());
        if (count == null || count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Achievement not found");
        }

        jdbc.update("""
            INSERT INTO achievement_xp_override
                (cooperative_id, achievement_id, xp_reward_override, updated_by, updated_at)
            VALUES (?, ?, ?, ?, NOW())
            ON CONFLICT (cooperative_id, achievement_id)
            DO UPDATE SET
                xp_reward_override = EXCLUDED.xp_reward_override,
                updated_by = EXCLUDED.updated_by,
                updated_at = NOW()
            """, cooperativeId, dto.getAchievementId(),
                dto.getXpReward(), authenticatedUser.getWorkerId());
    }

    public WorkerMonthSummaryDTO getWorkerMonthSummary(Long workerId, String yearMonth, Long cooperativeId) {
        if (authenticatedUser.isWorker()) {
            workerId = authenticatedUser.getWorkerId();
        }
        if (yearMonth == null) {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        List<Map<String, Object>> workerRows = jdbc.queryForList(
            "SELECT worker_name FROM workers WHERE worker_id = ? AND cooperative = ?",
            workerId, cooperativeId);
        if (workerRows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found in cooperative");
        }
        String workerName = (String) workerRows.get(0).get("worker_name");


        double totalWeightKg = jdbc.queryForObject("""
            SELECT COALESCE(SUM(m.weight_kg), 0)
            FROM measurements m
            WHERE m.wastepicker = ?
              AND TO_CHAR(m.time_stamp, 'YYYY-MM') = ?
            """, Double.class, workerId, yearMonth);

        int daysWorked = jdbc.queryForObject("""
            SELECT COUNT(DISTINCT DATE(m.time_stamp))
            FROM measurements m
            WHERE m.wastepicker = ?
              AND TO_CHAR(m.time_stamp, 'YYYY-MM') = ?
            """, Integer.class, workerId, yearMonth);

        final String ym = yearMonth;
        List<AchievementDTO> achievements = jdbc.query("""
            SELECT
                ad.achievement_id,
                ad.achievement_key,
                ad.achievement_name,
                ad.description,
                ad.category,
                ad.threshold_value,
                ad.difficulty,
                COALESCE(axo.xp_reward_override, ad.base_xp_reward) AS effective_xp,
                wa.progress_value,
                wa.unlocked_at
            FROM achievement_definition ad
            LEFT JOIN achievement_xp_override axo
                ON axo.achievement_id = ad.achievement_id
                AND axo.cooperative_id = ?
            LEFT JOIN worker_achievement wa
                ON wa.achievement_id = ad.achievement_id
                AND wa.worker_id = ?
                AND wa.year_month = ?
            ORDER BY ad.category, ad.threshold_value
            """, (rs, i) -> {
            AchievementDTO a = new AchievementDTO();
            a.setAchievementId(rs.getLong("achievement_id"));
            a.setAchievementKey(rs.getString("achievement_key"));
            a.setAchievementName(rs.getString("achievement_name"));
            a.setDescription(rs.getString("description"));
            a.setCategory(rs.getString("category"));
            a.setThresholdValue(rs.getDouble("threshold_value"));
            a.setDifficulty(rs.getString("difficulty"));
            a.setXpReward(rs.getInt("effective_xp"));
            a.setProgressValue(rs.getDouble("progress_value"));
            java.sql.Timestamp ts = rs.getTimestamp("unlocked_at");
            a.setUnlocked(ts != null);
            if (ts != null) a.setUnlockedAt(ts.toInstant());
            return a;
        }, cooperativeId, workerId, ym);

        int achievementsUnlocked = (int) achievements.stream()
            .filter(AchievementDTO::isUnlocked).count();
        int achievementXp = achievements.stream()
            .filter(AchievementDTO::isUnlocked)
            .mapToInt(AchievementDTO::getXpReward)
            .sum();

        WorkerMonthSummaryDTO summary = new WorkerMonthSummaryDTO();
        summary.setWorkerId(workerId);
        summary.setWorkerName(workerName);
        summary.setYearMonth(yearMonth);
        summary.setTotalWeightKg(totalWeightKg);
        summary.setDaysWorked(daysWorked);
        summary.setAchievementsUnlocked(achievementsUnlocked);
        summary.setTotalXpEarned(achievementXp);
        summary.setAchievements(achievements);
        return summary;
    }

    // Hent workers bedste måned i indeværende år (højest XP)
    public WorkerMonthSummaryDTO getTopMonthThisYear(Long workerId, Long cooperativeId) {
        if (authenticatedUser.isWorker()) {
            workerId = authenticatedUser.getWorkerId();
        }

        int currentYear = LocalDate.now().getYear();

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT
                wa.year_month,
                COALESCE(SUM(COALESCE(axo.xp_reward_override, ad.base_xp_reward)), 0) AS ach_xp
            FROM worker_achievement wa
            JOIN achievement_definition ad ON ad.achievement_id = wa.achievement_id
            LEFT JOIN achievement_xp_override axo
                ON axo.achievement_id = wa.achievement_id
                AND axo.cooperative_id = ?
            WHERE wa.worker_id = ?
              AND wa.unlocked_at IS NOT NULL
              AND wa.year_month LIKE ?
            GROUP BY wa.year_month
            ORDER BY ach_xp DESC
            LIMIT 1
            """, cooperativeId, workerId, currentYear + "-%");

        if (rows.isEmpty()) {
            return getWorkerMonthSummary(workerId,
                YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                cooperativeId);
        }

        String bestMonth = (String) rows.get(0).get("year_month");
        return getWorkerMonthSummary(workerId, bestMonth, cooperativeId);
    }

    public Map<String, Object> getTopDayInMonth(Long workerId, String yearMonth, Long cooperativeId) {
        if (authenticatedUser.isWorker()) {
            workerId = authenticatedUser.getWorkerId();
        }
        if (yearMonth == null) {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT
                DATE(m.time_stamp) AS work_date,
                SUM(m.weight_kg) AS total_kg
            FROM measurements m
            WHERE m.wastepicker = ?
              AND TO_CHAR(m.time_stamp, 'YYYY-MM') = ?
            GROUP BY DATE(m.time_stamp)
            ORDER BY total_kg DESC
            LIMIT 1
            """, workerId, yearMonth);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("workerId", workerId);
        result.put("yearMonth", yearMonth);
        if (rows.isEmpty()) {
            result.put("bestDate", null);
            result.put("totalKg", 0.0);
        } else {
            result.put("bestDate", rows.get(0).get("work_date").toString());
            result.put("totalKg", ((Number) rows.get(0).get("total_kg")).doubleValue());
        }
        return result;
    }

    // SCHEDULED - kaldes af AchievementEvaluationScheduler

    public void evaluateAchievementsForCooperative(long cooperativeId, String yearMonth) {
        List<Map<String, Object>> workers = jdbc.queryForList(
            "SELECT worker_id FROM workers WHERE cooperative = ? AND exit_date IS NULL",
            cooperativeId);

        for (Map<String, Object> workerRow : workers) {
            long workerId = ((Number) workerRow.get("worker_id")).longValue();
            evaluateAchievementsForWorker(workerId, cooperativeId, yearMonth);
        }
    }

    private void evaluateAchievementsForWorker(long workerId, long cooperativeId, String yearMonth) {
        double totalKg = jdbc.queryForObject("""
            SELECT COALESCE(SUM(weight_kg), 0)
            FROM measurements
            WHERE wastepicker = ?
              AND TO_CHAR(time_stamp, 'YYYY-MM') = ?
            """, Double.class, workerId, yearMonth);

        int daysWorked = jdbc.queryForObject("""
            SELECT COUNT(DISTINCT DATE(time_stamp))
            FROM measurements
            WHERE wastepicker = ?
              AND TO_CHAR(time_stamp, 'YYYY-MM') = ?
            """, Integer.class, workerId, yearMonth);

        List<Map<String, Object>> allAch = jdbc.queryForList("""
            SELECT achievement_id, category, threshold_value
            FROM achievement_definition
            ORDER BY category, threshold_value
            """);

        for (Map<String, Object> ach : allAch) {
            long achId = ((Number) ach.get("achievement_id")).longValue();
            String category = (String) ach.get("category");
            double threshold = ((Number) ach.get("threshold_value")).doubleValue();

            double progress = 0;
            switch (category) {
                case "WEIGHT"      -> progress = totalKg;
                case "DAYS_WORKED" -> progress = daysWorked;
                default            -> { continue; }
            }
            upsertAchievementProgress(workerId, achId, cooperativeId, yearMonth, progress, threshold);
        }

        // Evaluer ACHIEVEMENTS_COUNT achievements
        int unlockedCount = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM worker_achievement wa
            JOIN achievement_definition ad ON ad.achievement_id = wa.achievement_id
            WHERE wa.worker_id = ?
              AND wa.year_month = ?
              AND wa.cooperative_id = ?
              AND wa.unlocked_at IS NOT NULL
              AND ad.category != 'ACHIEVEMENTS_COUNT'
            """, Integer.class, workerId, yearMonth, cooperativeId);

        for (Map<String, Object> ach : allAch) {
            if (!"ACHIEVEMENTS_COUNT".equals(ach.get("category"))) continue;
            long achId = ((Number) ach.get("achievement_id")).longValue();
            double threshold = ((Number) ach.get("threshold_value")).doubleValue();
            upsertAchievementProgress(workerId, achId, cooperativeId, yearMonth, unlockedCount, threshold);
        }
    }

    private void upsertAchievementProgress(long workerId, long achId, long cooperativeId,
                                            String yearMonth, double progress, double threshold) {
        java.sql.Timestamp unlockedAt = progress >= threshold
            ? new java.sql.Timestamp(System.currentTimeMillis()) : null;

        jdbc.update("""
            INSERT INTO worker_achievement
                (worker_id, achievement_id, cooperative_id, year_month, progress_value, unlocked_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (worker_id, achievement_id, cooperative_id, year_month)
            DO UPDATE SET
                progress_value = EXCLUDED.progress_value,
                unlocked_at = CASE
                    WHEN worker_achievement.unlocked_at IS NULL AND EXCLUDED.unlocked_at IS NOT NULL
                    THEN EXCLUDED.unlocked_at
                    ELSE worker_achievement.unlocked_at
                END
            """, workerId, achId, cooperativeId, yearMonth, progress, unlockedAt);
    }
}