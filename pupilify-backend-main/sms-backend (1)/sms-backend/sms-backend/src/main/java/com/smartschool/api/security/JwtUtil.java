package com.smartschool.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long jwtExpirationMs;

    public JwtUtil(@Value("${app.jwt.secret:ChangeThisSecretKeyForProdDoNotShare}") String secret,
                   @Value("${app.jwt.expiration-ms:592000000}") long jwtExpirationMs) {
        if (secret == null || secret.isBlank() || secret.length() < 32) {
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // Generate token with username only (backward compatibility)
    public String generateToken(String username) {
        return generateToken(username, null, null);
    }

    // Generate token with username and role
    public String generateToken(String username, String role) {
        return generateToken(username, role, null);
    }

    // Generate token with username, role, and departmentId
    public String generateToken(String username, String role, Long departmentId) {
        Map<String, Object> claims = new HashMap<>();
        
        // ✅ Ensure role has ROLE_ prefix
        String fullRole = role;
        if (role != null && !role.startsWith("ROLE_")) {
            fullRole = "ROLE_" + role;
        }
        
        if (fullRole != null) {
            claims.put("role", fullRole);
            claims.put("roles", List.of(fullRole));
        }
        
        if (departmentId != null) {
            claims.put("departmentId", departmentId);
        }
        
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
        
        System.out.println("🔑 Token generated for: " + username + ", Role: " + fullRole);
        return token;
    }

    // Get role from token
    public String getRoleFromToken(String token) {
        final Claims claims = extractAllClaims(token);
        String role = (String) claims.get("role");
        if (role == null) {
            List<String> roles = (List<String>) claims.get("roles");
            if (roles != null && !roles.isEmpty()) {
                role = roles.get(0);
            }
        }
        return role;
    }

    // Get departmentId from token
    public Long getDepartmentIdFromToken(String token) {
        final Claims claims = extractAllClaims(token);
        Object deptId = claims.get("departmentId");
        if (deptId != null) {
            return Long.parseLong(deptId.toString());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean isTokenNearExpiration(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long fiveMinutesInMillis = 5 * 60 * 1000;
            return (expiration.getTime() - now.getTime()) < fiveMinutesInMillis;
        } catch (Exception e) {
            return true;
        }
    }
}