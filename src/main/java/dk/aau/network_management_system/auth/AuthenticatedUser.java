package dk.aau.network_management_system.auth;

public class AuthenticatedUser {
    
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
            .iterator().next().getAuthorities().replace("ROLE_", "");
    }

    



    }
}
