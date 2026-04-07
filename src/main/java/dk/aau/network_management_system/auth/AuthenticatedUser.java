package dk.aau.network_management_system.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    private JwtPrincipal getPrincipal() {
        return (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getCpf() { return getPrincipal().getCpf(); }
    public String getRole() { return getPrincipal().getRole(); }
    public Long getCooperativeId() { return getPrincipal().getCooperativeId(); }
    public Long getWorkerId() { return getPrincipal().getWorkerId(); }

    public boolean isAdmin() { return "A".equals(getRole()); }
    public boolean isManager() { return "M".equals(getRole()); }
    public boolean isWorker() { return "W".equals(getRole()); }
}