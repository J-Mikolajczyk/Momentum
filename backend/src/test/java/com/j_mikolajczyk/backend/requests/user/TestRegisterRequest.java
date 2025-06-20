package com.j_mikolajczyk.backend.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.RegisterRequest;


@SpringBootTest
public class TestRegisterRequest {

    @Test
    public void testCreate() {
        RegisterRequest registerRequest = new RegisterRequest("email@example.com", "password", "name");
        assertEquals("email@example.com", registerRequest.getEmail());
        assertEquals("password", registerRequest.getPassword());
        assertEquals("name", registerRequest.getName());
        assertNotNull(registerRequest.getUser());
    }
}
