package dk.aau.network_management_system.gamification;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
        
    )
}