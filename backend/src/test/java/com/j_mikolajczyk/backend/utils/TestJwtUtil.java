package com.j_mikolajczyk.backend.utils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.bson.types.ObjectId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.j_mikolajczyk.backend.utils.JwtUtil;
import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;

import java.util.Map;


@SpringBootTest
public class TestJwtUtil {

    @Autowired
    private JwtUtil jwtUtil;

    private static String email = "test@gmail.com";
    private static String password = "password";
    private static String name = "Test User";
    private static User user = new User(email, password, name);
    private static UserDTO userDTO = null;

    @BeforeAll
    public static void before() {
        user.setId(new ObjectId());
        userDTO = new UserDTO(user);
    }

    @Test
    public void testExtractEmailValid() {
        Map<String, String> tokens = jwtUtil.generateJwtToken(userDTO);
        String extractedEmail1 = jwtUtil.extractEmail(tokens.get("shortTermToken"));
        String extractedEmail2 = jwtUtil.extractEmail(tokens.get("longTermToken"));
        assertEquals(email, extractedEmail1);
        assertEquals(email, extractedEmail2);
    }

    @Test
    public void testExtractEmailNull() {
        String extractedEmail = jwtUtil.extractEmail(null);
        assertNull(extractedEmail);
    }

    @Test
    public void testGenerateToken() {
        Map<String, String> tokens = jwtUtil.generateJwtToken(userDTO);
        assertEquals(tokens.size(), 2);
    }


}