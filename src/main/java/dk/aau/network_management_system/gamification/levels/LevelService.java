package dk.aau.network_management_system.gamification.levels;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@Service
public class LevelService {

    private final JdbcTemplate jdbc;
    private final AuthenticatedUser authenticatedUser;
    private final PermissionHelper permissionHelper;

    public LevelService(JdbcTemplate jdbc,
        AuthenticatedUser authenticatedUser,
        PermissionHelper permissionHelper) {
            this.jdbc = jdbc;
            this.authenticatedUser = authenticatedUser;
            this.permissionHelper = permissionHelper;
        }


        // PUBLIC API

        // Alle level-definitioner
    public List<LevelDTO> getAllLevels() {
        return jdbc.query("""
            SELECT level_number, level_name, xp_required
            FROM level_definition
            ORDER BY level_number ASC
            """, (rs, i) -> {
                LevelDTO dto = new LevelDTO();
                dto.setLevelNumber(rs.getInt("level_number"));
                dto.setLevelName(rs.getString("level_name"));
                dto.setXpRequired(rs.getInt("xp_required"));
                return dto;
            });
        }

        // En workers nuværende level og total XP
    public LevelDTO getWorkerLevel(Long workerId) {
        if(authenticatedUser.isWorker()) {
            workerId = authenticatedUser.getWorkerId();
        }

        // Hent eller oprette worker_level 
        ensureWorkerLevelExists(workerId);

        final Long finalWorkerId = workerId;

        return jdbc.queryForObject("""
                SELECT
                    wl.worker_id,
                    wl.total_xp,
                    wl.current_level,
                    ld.level_name,
                    ld.xp_required,
                    next_ld.xp_required AS next_xp_required
                FROM worker_level wl
                JOIN level_definition ld ON ld.level_number = wl.current_level
                LEFT JOIN level_definition next_ld ON next_ld.level_number = wl.current_level + 1
                WHERE wl.worker_id = ?
            """, (rs, i) -> {
                LevelDTO dto = new LevelDTO();
                dto.setWorkerId(rs.getLong("worker_id"));
                dto.setTotalXp(rs.getInt("total_xp"));
                dto.setLevelNumber(rs.getInt("current_level"));
                dto.setLevelName(rs.getString("level_name"));
                dto.setXpRequired(rs.getInt("xp_required"));

                int nextXpRequired = rs.getInt("next_xp_required");
                if (rs.wasNull()) {
                    // Max level 10
                    dto.setXpToNext(0);
                } else {
                    dto.setXpToNext(nextXpRequired - rs.getInt("total_xp"));
                }
                dto.setCurrentLevel(true);
                return dto;
            }, finalWorkerId);

    }


    // Internal - called by AchievementEvaluationScheduler

    // Opdater en workers globale XP og level baseret på alle unlocked achivements
    // Kører efter achievement-evaluering så XP og level altid er opdateret

    public void recalculateWorkerLevel(long workerId, long cooperativeId) {
        ensureWorkerLevelExists(workerId);

        // Sum alt achievement XP (globalt - alle måneder)
        Integer totalXp = jdbc.queryForObject("""
                SELECT COALESCE(SUM(COALESCE(axo.xp_reward_override, ad.base_xp_reward)), 0)
                FROM worker_achievement wa
                JOIN achievement_definition ad ON ad.achievement_id = wa.achievement_id
                LEFT JOIN achievement_xp_override axo
                    ON axo.achievement_id = wa.achievement_id
                    AND axo.cooperative_id = ?
                WHERE wa.worker_id = ?
                    AND wa.unlocked_at IS NOT NULL
                """, Integer.class, cooperativeId, workerId);

        if (totalXp == null) totalXp = 0;

        // Find korrekte level baseret på total XP
        Integer newLevel = jdbc.queryForObject("""
                SELECT COALESCE(MAX(level_number), 1)
                FROM level_definition
                where xp_required <= ?
                """, Integer.class, totalXp);

        if (newLevel == null) newLevel = 1;

        jdbc.update("""
                UPDATE worker_level
                SET total_xp = ?, current_level = ?, last_updated = NOW()
                where worker_id = ?
                """, totalXp, newLevel, workerId);
    }


    // Private Helpers

    private void ensureWorkerLevelExists(long workerId) {
        jdbc.update("""
                INSERT INTO worker_level (worker_id, total_xp, current_level)
                VALUES (?, 0, 1)
                ON CONFLICT (worker_id) DO NOTHING
            """, workerId);
    }
}
