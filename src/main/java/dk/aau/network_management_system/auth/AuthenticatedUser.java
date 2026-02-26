package dk.aau.network_management_system.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    private final JwtUtil jwtUtil;

    public AuthenticatedUser(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    public String getCpf(){
        return getCurrentUserDetails().getUsername();
    }

    public String getRole(){
        return getCurrentUserDetails().getAuthorities()
            .iterator().next().getAuthority().replace("ROLE_", "");  // ← FEJL: .getAuthorities() i stedet for .getAuthority()
    }

    public Long getCooperativeId() {
        String token = extractTokenFromRequest();
        return jwtUtil.extractCooperativeId(token);
    }
    
    public Long getWorkerId() {
        String token = extractTokenFromRequest();
        return jwtUtil.extractWorkerId(token);
    }

    public boolean isAdmin() {
        return "A".equals(getRole());
    }
    
    public boolean isManager() {
        return "M".equals(getRole());
    }
    
    public boolean isWorker() {
        return "W".equals(getRole());
    }

    private UserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) auth.getPrincipal();
    }
    
    private String extractTokenFromRequest() {
        return JwtAuthFilter.getCurrentToken();
    }
}