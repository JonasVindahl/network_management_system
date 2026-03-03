package dk.aau.network_management_system.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorkerDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        String sql = "SELECT cpf, password, user_type FROM public.workers WHERE cpf = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, 
                cpf.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        System.out.println("Query returned " + rows.size() + " rows for CPF: " + cpf);

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("Worker not found with CPF: " + cpf);
        }

        Map<String, Object> worker = rows.get(0);

        String passwordStr = new String((byte[]) worker.get("password"), 
                java.nio.charset.StandardCharsets.UTF_8);
        String userType = (String) worker.get("user_type");

        System.out.println("Loaded user: " + cpf + " role: " + userType + " password starts with: " + passwordStr.substring(0, 7));

        return org.springframework.security.core.userdetails.User.builder()
                .username(cpf)
                .password(passwordStr)
                .roles(userType.trim())
                .build();
    }
}