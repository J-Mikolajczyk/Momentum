package com.j_mikolajczyk.backend.utils;

import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.services.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestAuthGuard {

    private UserService userService;
    private JwtUtil jwtUtil;
    private CookieUtil cookieUtil;
    private AuthGuard authGuard;
    private HttpServletRequest request;
    private ObjectId userId;
    private User user;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtUtil = mock(JwtUtil.class);
        cookieUtil = mock(CookieUtil.class);
        authGuard = new AuthGuard(userService, jwtUtil, cookieUtil);
        request = mock(HttpServletRequest.class);
        userId = new ObjectId();
        user = mock(User.class);
    }

    @Test
    void testUserNotFound() throws Exception {
        when(userService.getById(userId)).thenThrow(new RuntimeException("Not found"));
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testRequestUriNotSecure() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/auth");
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNull(response);
    }

    @Test
    void testNoJwtCookieFound() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/secure");
        when(cookieUtil.extractShortTermJwt(request)).thenReturn(null);
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("No JWT cookie found", response.getBody());
    }

    @Test
    void testInvalidJwt() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/secure");
        when(cookieUtil.extractShortTermJwt(request)).thenReturn("jwt");
        when(jwtUtil.extractEmail("jwt")).thenThrow(new RuntimeException("Invalid JWT"));
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid JWT", response.getBody());
    }

    @Test
    void testNoSubjectInJwt() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/secure");
        when(cookieUtil.extractShortTermJwt(request)).thenReturn("jwt");
        when(jwtUtil.extractEmail("jwt")).thenReturn(null);
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("No subject found in JWT.", response.getBody());
    }

    @Test
    void testWrongUsersCookie() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/secure");
        when(cookieUtil.extractShortTermJwt(request)).thenReturn("jwt");
        when(jwtUtil.extractEmail("jwt")).thenReturn("wrong@email.com");
        when(user.getEmail()).thenReturn("user@email.com");
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Wrong user's cookie", response.getBody());
    }

    @Test
    void testValidAccess() throws Exception {
        when(userService.getById(userId)).thenReturn(user);
        when(request.getRequestURI()).thenReturn("/secure");
        when(cookieUtil.extractShortTermJwt(request)).thenReturn("jwt");
        when(jwtUtil.extractEmail("jwt")).thenReturn("user@email.com");
        when(user.getEmail()).thenReturn("user@email.com");
        ResponseEntity<?> response = authGuard.validateUserAccess(userId, request);
        assertNull(response);
    }
}