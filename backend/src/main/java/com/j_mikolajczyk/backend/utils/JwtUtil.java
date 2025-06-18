package com.j_mikolajczyk.backend.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.shortTermExpiration}")
    private long shortTermExpiration;

    @Value("${jwt.longTermExpiration}")
    private long longTermExpiration;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Map<String, String> generateTokens(UserDTO userDTO) {
        Map<String, String> tokens = new HashMap<>();
            
        String shortTermToken = generateToken(userDTO, shortTermExpiration);
        String longTermToken = generateToken(userDTO, longTermExpiration);
    
        tokens.put("shortTermToken", shortTermToken);
        tokens.put("longTermToken", longTermToken);
    
        return tokens;
    }

    private String generateToken(UserDTO userDTO, long expiration) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
            
        String token = Jwts.builder()
                .setSubject(userDTO.getId())
                .claim("email", userDTO.getEmail())
                .claim("name", userDTO.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    
        return token;
    }

    public String generateShortTermToken(UserDTO userDTO) {
        return generateToken(userDTO, longTermExpiration);
    }

    public Claims validateToken(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        if (token == null || token.isEmpty()) {
            return true;
        }
        Claims claims = validateToken(token);
        if (claims == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    public String extractEmail(String token) {
        Claims claims = validateToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("email", String.class);
    }
}