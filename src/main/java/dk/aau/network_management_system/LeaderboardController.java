package dk.aau.network_management_system;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// RestController til API endpoints
@RestController
public class LeaderboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Returnerer top 3 workers i et cooperative rangeret efter samlet vægt (kg)
    // Eksempel: /getTop3?cooperativeId=1
    @GetMapping("/getTop3")
    public List<Map<String, Object>> getTop3(@RequestParam long cooperativeId) {

        String sql = """
            SELECT
                w.worker_id,
                w.worker_name,
                COALESCE(SUM(m.weight_kg), 0) AS total_weight_kg
            FROM public.workers w
            LEFT JOIN public.measurements m
                ON m.wastepicker = w.worker_id
            WHERE w.cooperative = ?
            GROUP BY w.worker_id, w.worker_name
            ORDER BY total_weight_kg DESC
            LIMIT 3
            """;

        return jdbcTemplate.queryForList(sql, cooperativeId);
    }
}