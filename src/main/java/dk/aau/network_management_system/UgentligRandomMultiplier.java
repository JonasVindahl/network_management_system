package dk.aau.network_management_system;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class UgentligRandomMultiplier {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public final  Random random = new Random();

    @Scheduled(cron = "10 04 13 * * THU")
    public void weeklyMultiplier() {
        String sql = """
                SELECT cooperative_id
                FROM cooperative
                """;
        List<Map<String, Object>> cooperatives = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> cooperative : cooperatives) {
            long cooperativeId = ((Number) cooperative.get("cooperative_id")).longValue();
            double newMultiplier = 0.8 + (1.5 - 0.8) * random.nextDouble();

            // Hvis rækken ikke findes indsætter en ny. Hvis rækken allerede findes opdatér den eksisterende
            String upsertSql = """
                    INSERT INTO cooperative_random_multiplier
                        (cooperative_id, multiplier_value, last_updated)
                    VALUES (?, ?, NOW())
                    ON CONFLICT (cooperative_id) 
                    DO UPDATE SET 
                        multiplier_value = EXCLUDED.multiplier_value, --EXLUDED er til nye værdier
                        last_updated = NOW()
                    """;

            jdbcTemplate.update(upsertSql, cooperativeId, newMultiplier);
        }
    }
}