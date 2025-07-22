package org.example.economily.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String KEY = "oqJgWZodnCuUJDPsF0GHk2qL/4zH0dBRdgpOJPSv7zJ0GpQYri5slbyQ2e0NjCu1QK8q1O7xBkd+VD1xA5AVPw==";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(KEY));

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10 * 24)) // 10 kun
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .setAllowedClockSkewSeconds(30)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String getSecretKeyAsString() {
        return Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded());
    }
}