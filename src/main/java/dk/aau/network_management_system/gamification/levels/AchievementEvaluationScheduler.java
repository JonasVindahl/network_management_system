package dk.aau.network_management_system.gamification.levels;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.aau.network_management_system.gamification.achievements.AchievementService;

// Kører hver nat kl.2:00
// 1. Evaluerer achievement-progress for alle workers i alle kooperativer
// 2. Genberegner workers globale level baseret på XP

@EnableScheduling
@Component

public class AchievementEvaluationScheduler {

    private final JdbcTemplate jdbc;
    private final AchievementService achievementService;
    private final LevelService levelService;

    public AchievementEvaluationScheduler(
        JdbcTemplate jdbc,
        AchievementService achievementService,
        LevelService levelService) {
    
    this.jdbc = jdbc;
    this.achievementService = achievementService;
    this.levelService = levelService;
        }

    @Scheduled(cron = "0 0 2 * * *")
    public void evaluateCurrentMonth() {
        String yearMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        List<Map<String, Object>> cooperatives = jdbc.queryForList(
            "SELECT cooperative_id FROM cooperative");

        for (Map<String, Object> coop : cooperatives) {
            long cooperativeId = ((Number) coop.get("cooperative_id")).longValue();

            // 1. Evaluate achievements for alle workers i dette kooperativ
            achievementService.evaluateAchievementsForCooperative(cooperativeId, yearMonth);

            // 2. Genberegn level for alle aktive workers i dette kooperativ
            List<Map<String, Object>> workers = jdbc.queryForList(
                "SELECT worker_id FROM workers WHERE cooperative = ? and exit_date IS NULL",
                cooperativeId);

            
            for (Map<String, Object> worker : workers) {
                long workerId = ((Number) worker.get("worker_id")).longValue();
                levelService.recalculateWorkerLevel(workerId, cooperativeId);
            }


        }
    }


}