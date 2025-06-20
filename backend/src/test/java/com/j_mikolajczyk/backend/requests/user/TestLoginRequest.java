package com.j_mikolajczyk.backend.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.LoginRequest;


@SpringBootTest
public class TestLoginRequest {

    @Test
    public void testCreateEmpty() {
        LoginRequest loginRequest = new LoginRequest();
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());
    }

    @Test
    public void testCreateArgs() {
        LoginRequest loginRequest = new LoginRequest("email@example.com", "password");
        assertEquals("email@example.com", loginRequest.getEmail());
        assertEquals("password", loginRequest.getPassword());
    }
}
