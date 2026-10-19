package com.newasteralnx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;
    /** 7 days for Remember Me */
    private static final long REMEMBER_ME_MS = 7L * 24 * 60 * 60 * 1000;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String email, String role) {
        return generateToken(userId, email, role, false);
    }

    public String generateToken(Long userId, String email, String role, boolean rememberMe) {
        Date now = new Date();
        long ttl = rememberMe ? REMEMBER_ME_MS : expiration;
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .claim("rm", rememberMe)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl))
                .signWith(secretKey)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}
