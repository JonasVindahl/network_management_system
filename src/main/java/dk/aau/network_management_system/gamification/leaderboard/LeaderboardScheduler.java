package dk.aau.network_management_system.gamification.leaderboard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

// Beregner og gemmer leaderboard snapshots hver 7. dag
// Kører på 7, 14, 21, 28 kl 3.00
// API læser fra db - ikke live

@EnableScheduling
@Component

public class LeaderboardScheduler {
    private final JdbcTemplate jdbc;
    private final LeaderboardService leaderboardService;

    public LeaderboardScheduler(JdbcTemplate jdbc, LeaderboardService leaderboardService) {
        this.jdbc = jdbc;
        this.leaderboardService = leaderboardService;
    }

    // Bestemmer hvilken uge der er afsluttet og beregner snapshot
    @Scheduled(cron = "0 0 3 7,14,21,28 * *")
    public void computeWeeklySnapshot() {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        String yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        int weekNumber = switch (day) {
            case 7 -> 1;
            case 14 -> 2;
            case 21 -> 3;
            case 28 -> 4;
            default -> -1;

        };

        if (weekNumber == -1) return;

        computeForAllCooperatives(yearMonth, weekNumber);
    }

    // Kører den 1. i måneden kl3
    // Færdiggør previous måneds uge-4 snapshot (vinderen vises dag 1-7?)
    @Scheduled(cron = "0 0 3 1 * *")
    public void computePreviousMonthFinalSnapshot() {
        YearMonth prevMonth = YearMonth.now().minusMonths(1);
        String yearMonth = prevMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        computeForAllCooperatives(yearMonth, 4);
    }

    // Private helpers

    private void computeForAllCooperatives(String yearMonth, int weekNumber) {
        List<Map<String, Object>> cooperatives = jdbc.queryForList("SELECT cooperative_id FROM cooperative");

        for (Map<String, Object> coop : cooperatives) {
            long cooperativeId = ((Number) coop.get("cooperative_id")).longValue();
            try {
                leaderboardService.computeAndPersistSnapshot(cooperativeId, yearMonth, weekNumber);
            } catch (Exception e) {
                // Log fejl
                System.err.println("[LeaderboardScheduler] Error for cooperative " + cooperativeId + ": " + e.getMessage());
            }
        }
    }
}

