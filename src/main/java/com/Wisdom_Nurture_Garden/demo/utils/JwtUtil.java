package com.Wisdom_Nurture_Garden.demo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("MySuperSecretKeyForJwtTokenWisdomGarden".getBytes());
    private static final long EXPIRATION_TIME = 30 * 24 * 60 * 60 * 1000L; // 一个月

    public static String generateToken(Integer id, String username, int role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    public static Integer getRole(String token) {
        return parseToken(token).get("role", Integer.class);
    }
}
