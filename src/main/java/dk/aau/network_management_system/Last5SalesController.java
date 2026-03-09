package dk.aau.network_management_system;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@RestController
public class Last5SalesController   {

    private final JdbcTemplate jdbcTemplate;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public Last5SalesController(JdbcTemplate jdbcTemplate, AuthenticatedUser authenticatedUser) {
        this.jdbcTemplate = jdbcTemplate;
        this.authenticatedUser = authenticatedUser;
    }

    // /getLast5Sales?materialId=1
    @GetMapping("/getLast5Sales")
    public List<Map<String, Object>> getLast5Sales(@RequestParam long materialId) {

        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Workers may not see Last 5 sales!");
        }

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