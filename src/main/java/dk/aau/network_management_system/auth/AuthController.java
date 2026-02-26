package dk.aau.network_management_system.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WorkerDetailsService workerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCpf(), request.getPassword()));

        UserDetails userDetails = workerDetailsService.loadUserByUsername(request.getCpf());
        String role = userDetails.getAuthorities().iterator().next().getAuthority()
                .replace("ROLE_", "");

        String token = jwtUtil.generateToken(request.getCpf(), role);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}