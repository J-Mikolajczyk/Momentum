package com.j_mikolajczyk.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;
import com.j_mikolajczyk.backend.utils.CookieUtil;
import com.j_mikolajczyk.backend.utils.JwtUtil;
import com.j_mikolajczyk.backend.requests.user.LoginRequest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TestUserController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CookieUtil cookieUtil;

    @MockBean
    private AuthGuard authGuard;

    @Autowired
    private ObjectMapper objectMapper;

    private ObjectId userId;
    private String jwt;
    private UserDTO mockUserDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        userId = new ObjectId();
        jwt = "mocked.jwt.token";
        mockUser = new User();
        mockUser.setId(userId);
        mockUserDTO = new UserDTO(mockUser);
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(userService.refresh(userId))
            .thenReturn(mockUserDTO);

        mockMvc.perform(get("/secure/user/refresh")
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(mockUserDTO)));
    }

    @Test
    void testLoginAuthFailure() throws Exception {
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));

        mockMvc.perform(get("/secure/user/refresh")
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginNotFound() throws Exception {
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(userService.refresh(userId))
            .thenThrow(new NotFoundException());

        mockMvc.perform(get("/secure/user/refresh")
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testLoginExcpetion() throws Exception {
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(userService.refresh(userId))
            .thenThrow(new Exception());

        mockMvc.perform(get("/secure/user/refresh")
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSuccess() throws Exception {
        LoginRequest request = new LoginRequest("email", "password");

        when(userService.getObjectIdByEmail("email"))
            .thenReturn(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(userService).delete(eq(userId), eq("password"));

        mockMvc.perform(post("/secure/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"deleted\": true}"));
    }

    @Test
    void testDeleteNotFound() throws Exception {
        LoginRequest request = new LoginRequest("email", "password");

        when(userService.getObjectIdByEmail("email"))
            .thenThrow(new NotFoundException());

        mockMvc.perform(post("/secure/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testDeleteAuthFailure() throws Exception {
        LoginRequest request = new LoginRequest("email", "password");

        when(userService.getObjectIdByEmail("email"))
            .thenReturn(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        
        mockMvc.perform(post("/secure/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("email", "password");

        when(userService.getObjectIdByEmail("email"))
            .thenReturn(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new BadCredentialsException("Invalid credentials")).when(userService).delete(eq(userId), eq("password"));

        mockMvc.perform(post("/secure/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteException() throws Exception {
        LoginRequest request = new LoginRequest("email", "password");

        when(userService.getObjectIdByEmail("email"))
            .thenReturn(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new Exception()).when(userService).delete(eq(userId), eq("password"));

        mockMvc.perform(post("/secure/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }
}
