package com.j_mikolajczyk.backend.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.j_mikolajczyk.backend.dto.UserDTO;

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

    public Map<String, String> generateJwtToken(UserDTO userDTO) {
        Map<String, String> tokens = new HashMap<>();
    
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
            
        String shortTermToken = Jwts.builder()
                .setSubject(userDTO.getId())
                .claim("email", userDTO.getEmail())
                .claim("name", userDTO.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + shortTermExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String longTermToken = Jwts.builder()
                    .setSubject(userDTO.getId())
                .claim("email", userDTO.getEmail())
                .claim("name", userDTO.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + longTermExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    
        tokens.put("shortTermToken", shortTermToken);
        tokens.put("longTermToken", longTermToken);
    
        return tokens;
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return validateToken(token).getExpiration().before(new Date());
    }
}