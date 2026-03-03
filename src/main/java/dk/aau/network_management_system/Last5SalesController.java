package dk.aau.network_management_system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

// RestController til API endpoints
@RestController
public class Last5SalesController   {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/materials/{id}/sales")
    public List<Map<String, Object>> getLast5Sales(@PathVariable long id) {
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
        return  jdbcTemplate.queryForList(sql, id);
    }
}