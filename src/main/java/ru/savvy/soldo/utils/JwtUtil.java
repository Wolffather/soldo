package ru.savvy.soldo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import ru.savvy.soldo.enums.UserRole;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtil {
    
    /**
     * Генерация токена с ролями
     * @param username логин пользователя
     * @param UserRoles список ролей (enum или строк)
     * @return JWT токен
     */
    public static String generateToken(String username, List<UserRole> UserRoles) {
        Claims claims = Jwts.claims().setSubject(username);
        List<String> UserRoleNames = UserRoles.stream().map(UserRole::name).collect(Collectors.toList());
        claims.put("UserRoles", UserRoleNames);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .compact();
    }

    /**
     * Проверка валидности токена (без секретного ключа)
     * @param token JWT
     * @return true, если валиден
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Извлечение имени пользователя из токена
     * @param token JWT
     * @return логин
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                //.setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Извлечение ролей из токена
     * @param token JWT
     * @return список ролей
     */
    public static List<UserRole> getUserRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> UserRoleNames = (List<String>) claims.get("UserRoles");
        return UserRoleNames.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toList());
    }

    
}