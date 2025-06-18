package com.j_mikolajczyk.backend.utils;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;
import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestJwtUtil {

    @Autowired
    private JwtUtil jwtUtil;

    private static String email = "test@gmail.com";
    private static String password = "password";
    private static String name = "Test User";
    private static User user;
    private static UserDTO userDTO;

    @BeforeAll
    public static void setup() {
        user = new User(email, password, name);
        user.setId(new ObjectId());
        userDTO = new UserDTO(user);
    }

    @Test
    public void testGenerateTokens() {
        Map<String, String> tokens = jwtUtil.generateTokens(userDTO);
        assertNotNull(tokens.get("shortTermToken"));
        assertNotNull(tokens.get("longTermToken"));
        assertEquals(2, tokens.size());
    }

    @Test
    public void testExtractEmailValid() {
        Map<String, String> tokens = jwtUtil.generateTokens(userDTO);
        String extractedEmail1 = jwtUtil.extractEmail(tokens.get("shortTermToken"));
        String extractedEmail2 = jwtUtil.extractEmail(tokens.get("longTermToken"));
        assertEquals(email, extractedEmail1);
        assertEquals(email, extractedEmail2);
    }

    @Test
    public void testExtractEmailNull() {
        assertNull(jwtUtil.extractEmail(null));
    }

    @Test
    public void testIsTokenExpired() {
        String token = jwtUtil.generateShortTermToken(userDTO);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    public void testIsTokenExpiredWithNull() {
        assertTrue(jwtUtil.isTokenExpired(null));
        assertTrue(jwtUtil.isTokenExpired(""));
    }

    @Test
    public void testValidateToken() {
        String token = jwtUtil.generateShortTermToken(userDTO);
        Claims claims = jwtUtil.validateToken(token);
        assertNotNull(claims);
        assertEquals(email, claims.get("email", String.class));
        assertEquals(name, claims.get("name", String.class));
        assertEquals(user.getId().toHexString(), claims.getSubject());
    }

    @Test
    public void testGenerateShortTermToken() {
        String token = jwtUtil.generateShortTermToken(userDTO);
        assertNotNull(token);
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    @Test
    public void testExpiredToken() {
        String expiredToken = generateExpiredToken(userDTO);
        assertTrue(jwtUtil.isTokenExpired(expiredToken));
        assertNull(jwtUtil.validateToken(expiredToken));
    }

    private String generateExpiredToken(UserDTO userDTO) {
        try {
            java.lang.reflect.Method method = JwtUtil.class.getDeclaredMethod("generateToken", UserDTO.class, long.class);
            method.setAccessible(true);
            return (String) method.invoke(jwtUtil, userDTO, -1000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}