package com.eyedia.eyedia.config;

public class JwtUtil {

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey("your-secret-key") // JWT 서명 키
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
}