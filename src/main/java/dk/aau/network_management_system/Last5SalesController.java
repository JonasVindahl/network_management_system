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
public class Last5SalesController   {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/getLast5Sales")
    // http://localhost:8080/getLast5Sales?materialId=1
    public List<Map<String, Object>> getLast5Sales(@RequestParam long materialId) {
        String sql = """
                SELECT
                    material,
                    weight,
                    price_kg,
                    date
                FROM sales
                WHERE material = ?
                    ORDER BY date DESC
                    LIMIT 5
                """;
        return  jdbcTemplate.queryForList(sql, materialId);
    }
}