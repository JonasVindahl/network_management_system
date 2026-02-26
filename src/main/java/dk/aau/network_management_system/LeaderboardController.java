package dk.aau.network_management_system;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Markerer klassen som en REST API controller
@RestController
public class LeaderboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Endpoint der svarer på GET requests til /getTop3?cooperativeId=1
    @GetMapping("/api/getTop3")
    public List<Map<String, Object>> getTop3(@RequestParam long cooperativeId) {

        // SQL der henter alle målinger per worker per materiale
        // COALESCE bruges som fallback hvis værdien er NULL (bruger 0 eller 1.0 i stedet)
        // material_xp = total vægt * material multiplier
        String sql = """
            SELECT
                w.worker_id,
                w.worker_name,
                mat.material_name,
                cmm.multiplier_value,
                COALESCE(SUM(m.weight_kg), 0) * COALESCE(cmm.multiplier_value, 1.0) AS material_xp
            FROM public.workers w
            LEFT JOIN public.measurements m
                ON m.wastepicker = w.worker_id
            LEFT JOIN public.materials mat
                ON mat.material_id = m.material
            LEFT JOIN public.cooperative_material_multiplier cmm
                ON cmm.cooperative_id = w.cooperative
                AND cmm.material_id = m.material
            WHERE w.cooperative = ?
            GROUP BY w.worker_id, w.worker_name, mat.material_name, cmm.multiplier_value
            ORDER BY material_xp DESC
            """;

        // SQL der henter den ugentlige tilfældige multiplier for kooperativet
        String multipliersql = """
            SELECT multiplier_value
            FROM public.cooperative_random_multiplier
            WHERE cooperative_id = ?
        """;

        // Udfører sql og gemmer resultatet som en liste af rækker Hver række er et Map hvor nøglen er kolonnenavnet og værdien er data
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, cooperativeId);

        double randomMultiplier = jdbcTemplate.queryForObject(multipliersql, Double.class, cooperativeId);

        Map<Long, Map<String, Object>> workerMap = new LinkedHashMap<>();

        // Looper igennem alle rækker fra databasen. Hver række indeholder en workers målinger for et materiale
        for (Map<String, Object> row : rows) {
            // Henter worker_id fra rækken og caster til long
            long workerId = ((Number) row.get("worker_id")).longValue();
            // Henter worker_name fra rækken
            String workerName = (String) row.get("worker_name");
            // Henter material_xp (vægt * multiplier) fra rækken og caster til double
            double materialXp = ((Number) row.get("material_xp")).doubleValue();

            // Opretter en ny række for workeren hvis den ikke findes endnu
            workerMap.putIfAbsent(workerId, new LinkedHashMap<>());
            // Gemmer worker_id og worker_name på workeren
            workerMap.get(workerId).put("worker_id", workerId);
            workerMap.get(workerId).put("worker_name", workerName);

            // Henter den hidtidige XP for workeren (0.0 hvis ingen endnu) og lægger det nye materiales XP til
            double currentXp = ((Number) workerMap.get(workerId).getOrDefault("raw_xp", 0.0)).doubleValue();
            workerMap.get(workerId).put("raw_xp", currentXp + materialXp);
        }

        // Konverterer workerMap til en liste så vi kan sortere den
        List<Map<String, Object>> result = new ArrayList<>(workerMap.values());

        // Looper igennem alle workers og beregner den endelige XP. xp = raw_xp * randomMultiplier (den ugentlige tilfældige multiplier)
        for (Map<String, Object> worker : result) {
            double rawXp = ((Number) worker.get("raw_xp")).doubleValue();
            worker.put("xp", rawXp * randomMultiplier);
        }

        result.sort((a, b) -> Double.compare(((Number) b.get("xp")).doubleValue(), ((Number) a.get("xp")).doubleValue()));

        return result.stream().limit(3).collect(java.util.stream.Collectors.toList());
    }
}