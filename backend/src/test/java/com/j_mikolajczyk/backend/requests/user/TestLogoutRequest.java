package com.j_mikolajczyk.backend.requests.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.j_mikolajczyk.backend.requests.user.LogoutRequest;


@SpringBootTest
public class TestLogoutRequest {

    @Test
    public void testCreateEmpty() {
        LogoutRequest logoutRequest = new LogoutRequest();
        assertNull(logoutRequest.getEmail());
    }

    @Test
    public void testCreateEmail() {
        LogoutRequest logoutRequest = new LogoutRequest("email@example.com");
        assertEquals("email@example.com", logoutRequest.getEmail());
    }
}
