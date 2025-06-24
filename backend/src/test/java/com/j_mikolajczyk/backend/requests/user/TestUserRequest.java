package com.j_mikolajczyk.backend.requests.user;

import java.beans.Transient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import com.j_mikolajczyk.backend.requests.user.UserRequest;


@SpringBootTest
public class TestUserRequest { 

    @Test
    public void testCreateEmpty() {
        UserRequest userRequest = new UserRequest();
        assertNull(userRequest.getEmail());
    }

    @Test
    public void testCreateWithEmail() {
        UserRequest userRequest = new UserRequest("email@example.com");
        assertEquals("email@example.com", userRequest.getEmail());
    }

}