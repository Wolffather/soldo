package ru.savvy.soldo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static String staticSecretKey;

    @Value("${jwt.secret}")
    public void setSecretKey(String secretKey) {
        JwtUtil.staticSecretKey = secretKey; // присваиваем статическому полю
    }
    
    public static String generateToken(String username, String userRole) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("UserRole", userRole);
        claims.put("Username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(staticSecretKey.getBytes()))
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
           
            Jwts.parserBuilder()
                    .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(staticSecretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
       
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(staticSecretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static String getUserRoleFromToken(String token) {
       
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(staticSecretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get("UserRole");
    }
}