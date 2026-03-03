package dk.aau.network_management_system.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String cpf, String role, Long cooperativeId, Long workerId) {
        return Jwts.builder()
            .subject(cpf)
            .claim("role", role)
            .claim("cooperativeId", cooperativeId)
            .claim("workerId", workerId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }



    public String extractCpf(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long extractCooperativeId(String token) {
    return parseClaims(token).get("cooperativeId", Long.class);
    }

    public Long extractWorkerId(String token) {
        return parseClaims(token).get("workerId", Long.class);
    }



    public boolean isTokenValid(String token) {
        try {
            parseClaims(token); // throws if expired or invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}