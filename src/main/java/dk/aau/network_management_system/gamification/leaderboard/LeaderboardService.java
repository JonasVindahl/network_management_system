package dk.aau.network_management_system.gamification.leaderboard;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@Service
public class LeaderboardService {
    private final JdbcTemplate jdbc;
    private final AuthenticatedUser authenticatedUser;
    private final PermissionHelper permissionHelper;

    public LeaderboardService(JdbcTemplate jdbc, AuthenticatedUser authenticatedUser, PermissionHelper permissionHelper) {
        this.jdbc = jdbc;
        this.authenticatedUser = authenticatedUser;
        this.permissionHelper = permissionHelper;
    }

    // Public API 
    // Logik 
    // Dag 1-7 -> sidste måneds vinder 
    // Dag 7-14 -> top 3 for uge 1
    // Dag 14-21 -> top 3 for uge 2
    // Dag 21-28 -> top 3 for uge 3
    // Dag 28+ -> top 3 for uge 4

    public LeaderboardDTO getCurrentLeaderboard(Long cooperativeId) {
        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();

        String yearMonth;
        int weekNumber;

        if (dayOfMonth <= 7) {
            YearMonth prevMonth = YearMonth.now().minusMonths(1);
            yearMonth = prevMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            weekNumber = 4;
        } else if (dayOfMonth <= 14) {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            weekNumber = 1;
        } else if (dayOfMonth <= 21) {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            weekNumber = 2;
        } else if (dayOfMonth <= 28) {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            weekNumber = 3;
        } else {
            yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            weekNumber = 4;
        }

        return getLeaderboardFromDb(cooperativeId, yearMonth, weekNumber);
    }

    // Hent et specifikt historisk snapshot
    public LeaderboardDTO getLeaderboard(Long cooperativeId, String yearMonth, int weekNumber) {
        return getLeaderboardFromDb(cooperativeId, yearMonth, weekNumber);
    }

    // Internal
    // Beregn og gem er leaderboard snapshot -> XP = (weight_kg * material_multiplier + achievement_xp) * random_multiplier
    public void computeAndPersistSnapshot(long cooperativeId, String yearMonth, int weekNumber) {
        LocalDate[] range = weekRange(yearMonth, weekNumber);
        LocalDate weekStart = range[0];
        LocalDate weekEnd = range [1];

        // Weight XP per worker (uge * material_multiplier)
        List<Map<String, Object>> weightRows = jdbc.queryForList("""
                SELECT
                    w.worker_id,
                    w.worker_name,
                    COALESCE(SUM(m.weight_kg * COALESCE(cmm.multiplier_value, 1.0)), 0) AS weight_xp
                    FROM workers w
                    LEFT JOIN measurements m
                        ON m.wastepicker = w.worker_id
                        AND DATE(m.time_stamp) >= ?
                        AND DATE(m.time_stamp) <= ?
                    LEFT JOIN cooperative_material_multiplier cmm
                        ON cmm.cooperative_id = w.cooperative
                        AND cmm.material_id = m.material
                    WHERE w.cooperative = ?
                        AND w.exit_date IS NULL
                    GROUP BY w.worker_id, w.worker_name
                """, weekStart, weekEnd, cooperativeId);

                // Random multiplier for dette kooperativ
                double randomMultiplier = 1.0;
                try {
                    randomMultiplier = jdbc.queryForObject("""
                            SELECT multiplier_value
                            FROM cooperative_random_multiplier
                            WHERE cooperative_id = ?
                            """, Double.class, cooperativeId);
                } catch (Exception ignored) { }

                // Achievement XP optjent denne måned per worker
                Map<Long, Double> achXpByWorker = new HashMap<>();
                jdbc.query("""
                        SELECT
                            wa.worker_id,
                            COALESCE(SUM(COAwLESCE(axo.xp_reward_override, ad.base_xp_reward)), 0) AS ach_xp
                        FROM worker_achievement wa
                        JOIN achievement_definition ad ON ad.achievement_id = wa.achievement_id
                        LEFT JOIN achievement_xp_override axo
                            ON axo.achievement_id = wa.achievement_id
                            AND axo.cooperative_id = ?
                        WHERE wa.cooperative_id = ?
                            AND wa.year_month = ?
                            AND wa.unlocked_at IS NOT NULL
                        GROUP BY wa.worker_id
                        """, rs -> {
                            achXpByWorker.put(rs.getLong("worker_id"), rs.getDouble("ach_xp")); }, cooperativeId, cooperativeId, yearMonth);

                // Beregn endelig XP per worker og sorter
                final double finalRandomMult = randomMultiplier;
                List<WorkerScore> scores = weightRows.stream().map(row -> {
                    long workerId = ((Number) row.get("worker_id")).longValue();
                    String name = (String) row.get("worker_name");
                    double weightXp = ((Number) row.get("weight_xp")).doubleValue();
                    double achXp = achXpByWorker.getOrDefault(workerId, 0.0);
                    double rawXp = weightXp + achXp;
                    double finalXp = rawXp * finalRandomMult;
                    return new WorkerScore(workerId, name, rawXp, finalXp);
                }).sorted(Comparator.comparingDouble(WorkerScore::finalXp).reversed())
                .toList();

                // Insert and Update snapshot
                Long snapshotId = jdbc.queryForObject("""
                        INSERT INTO leaderboard_snapshot
                            (cooperative_id, year_month, week_number, computed_at)
                            VALUES(?, ?, ?, NOW())
                            ON CONFLICT (cooperative_id, year_month, week_number)
                            DO UPDATE SET computed_at = NOW()
                            RETURNING snapshot_id
                        """, Long.class, cooperativeId, yearMonth, weekNumber);

                        jdbc.update("DELETE FROM leaderboard_entry WHERE snapshot_id = ?", snapshotId);

                        // Indsæt top 3
                        int rank = 1;
                        for (WorkerScore score : scores.stream().limit(3).toList()) {
                            jdbc.update("""
                                    INSERT INTO leaderboard_entry
                                    (snapshot_id, rank_position, worker_id, worker_name, raw_xp, final_xp, random_mult)
                                    VALUES(?, ?, ?, ?, ?, ?, ?)
                                    """,
                                    snapshotId, rank++, score.workerId(), score.workerName(), score.rawXp(), score.finalXp(), finalRandomMult);
                        }


    }

    // Private helpers

    private LeaderboardDTO getLeaderboardFromDb(long cooperativeId, String yearMonth, int weekNumber) {
        List<Map<String, Object>> snapshots = jdbc.queryForList("""
            SELECT snapshot_id, computed_at
            FROM leaderboard_snapshot
            WHERE cooperative_id = ?
                AND year_month = ?
                AND week_number = ?
                """, cooperativeId, yearMonth, weekNumber);

        LeaderboardDTO dto = new LeaderboardDTO();
        dto.setYearMonth(yearMonth);
        dto.setWeekNumber(weekNumber);

        if (snapshots.isEmpty()) {
            dto.setEntries(Collections.emptyList());
            return dto;
        }

        Map<String, Object> snap = snapshots.get(0);
        long snapshotId = ((Number) snap.get("snapshot_id")).longValue();
        dto.setComputedAt(((java.sql.Timestamp) snap.get("computed_at")).toInstant());

        List<LeaderboardDTO.LeaderboardEntryDTO> entries = jdbc.query("""
                SELECT rank_position, worker_id, worker_name, raw_xp, final_xp, random_mult
                FROM leaderboard_entry
                WHERE snapshot_id = ?
                ORDER BY rank_position ASC
        """, (rs, i) -> {
            LeaderboardDTO.LeaderboardEntryDTO e = new LeaderboardDTO.LeaderboardEntryDTO();
            e.setRankPosition(rs.getInt("rank_position"));
            e.setWorkerId(rs.getLong("worker_id"));
            e.setWorkerName(rs.getString("worker_name"));
            e.setRawXP(rs.getDouble("raw_xp"));
            e.setFinalXP(rs.getDouble("final_xp"));
            e.setRandomMultiplier(rs.getDouble("random_mult"));
            return e;
        }, snapshotId);

        dto.setEntries(entries);
        return dto;
    }

    // Dato-range for en uge blok inden for en måned
    // uge 1 = dag 1-7, uge 2 = dag 8-14, uge 3 = dag 15-21, uge 4 = dag 22-slut
    private LocalDate[] weekRange(String yearMonth, int weekNumber) {
        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        return switch (weekNumber) {
            case 1 -> new LocalDate[]{ ym.atDay(1), ym.atDay(7) };
            case 2 -> new LocalDate[]{ ym.atDay(8), ym.atDay(14) };
            case 3 -> new LocalDate[]{ ym.atDay(15), ym.atDay(21) };
            case 4 -> new LocalDate[]{ ym.atDay(22), ym.atEndOfMonth() };
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weekNumber has to be 1-4");
        };
    }

    private record WorkerScore(long workerId, String workerName, double rawXp, double finalXp) {}
}

