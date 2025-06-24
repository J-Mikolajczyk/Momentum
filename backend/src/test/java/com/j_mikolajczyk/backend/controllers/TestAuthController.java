package com.j_mikolajczyk.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.exceptions.UserAlreadyExistsException;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.requests.user.LoginRequest;
import com.j_mikolajczyk.backend.requests.user.LogoutRequest;
import com.j_mikolajczyk.backend.requests.user.RegisterRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;
import com.j_mikolajczyk.backend.utils.CookieUtil;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import io.jsonwebtoken.Claims;

import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingBlockService blockService;

    @MockBean
    private TrainingBlockRepository blockRepository;

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

    private String email;
    private String password;
    private String name;
    private User user;
    private UserDTO userDTO;
    private ObjectId userId;

    @BeforeEach
    void setUp() {
        email = "email";
        password = "password";
        name = "name";
        user = new User(name, email, password);
        userId = new ObjectId();
        user.setId(userId);
        userDTO = new UserDTO(user);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(email, password, name);

        doNothing().when(userService).register(request);

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void testRegisterConflict() throws Exception {
        RegisterRequest request = new RegisterRequest(email, password, name);

        doThrow(new UserAlreadyExistsException("409")).when(userService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void testRegisterException() throws Exception {
        RegisterRequest request = new RegisterRequest(email, password, name);

        doThrow(new Exception()).when(userService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        when(userService.login(any(LoginRequest.class)))
            .thenReturn(userDTO);
        when(jwtUtil.generateTokens(any(UserDTO.class)))
            .thenReturn(Map.of("longTermToken", "longTermToken", "shortTermToken", "shortTermToken"));
        when(cookieUtil.createCookie(anyString(), anyString(), anyInt(), anyString()))
            .thenReturn(new Cookie("longTermCookie", "longTermToken"));
        
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void testLoginNotFound() throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        when(userService.login(any(LoginRequest.class)))
            .thenThrow(new NotFoundException());
        
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        when(userService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginException() throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        when(userService.login(any(LoginRequest.class)))
            .thenThrow(new Exception());
        
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testAutoLoginSuccess() throws Exception {
        String longTermToken = "validLongTermToken";
        Cookie longTermCookie = new Cookie("longTermCookie", longTermToken);

        when(jwtUtil.isTokenExpired(longTermToken)).thenReturn(false);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken(longTermToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(user.getId().toHexString());

        when(userService.getById(any(ObjectId.class))).thenReturn(user);
        when(jwtUtil.generateShortTermToken(any(UserDTO.class))).thenReturn("shortTermToken");
        when(cookieUtil.createCookie(eq("shortTermCookie"), eq("shortTermToken"), anyInt(), eq("/secure")))
            .thenReturn(new Cookie("shortTermCookie", "shortTermToken"));

        mockMvc.perform(post("/auth/auto-login")
                .cookie(longTermCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(user.getId().toHexString()))
            .andExpect(jsonPath("$.email").value(user.getEmail()))
            .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    void testAutoLoginNullCookies() throws Exception {
        mockMvc.perform(post("/auth/auto-login"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLoginNotLongTerm() throws Exception {
        Cookie shortTermCookie = new Cookie("shortTermCookie", null);

        mockMvc.perform(post("/auth/auto-login")
                .cookie(shortTermCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLoginNullToken() throws Exception {
        Cookie longTermCookie = new Cookie("longTermCookie", null);

        mockMvc.perform(post("/auth/auto-login")
                .cookie(longTermCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLoginExpiredToken() throws Exception {
        String longTermToken = "validLongTermToken";
        Cookie longTermCookie = new Cookie("longTermCookie", longTermToken);

        when(jwtUtil.isTokenExpired(longTermToken)).thenReturn(true);

        mockMvc.perform(post("/auth/auto-login")
                .cookie(longTermCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLoginNullClaim() throws Exception {
        String longTermToken = "validLongTermToken";
        Cookie longTermCookie = new Cookie("longTermCookie", longTermToken);

        when(jwtUtil.isTokenExpired(longTermToken)).thenReturn(false);
        when(jwtUtil.validateToken(longTermToken)).thenReturn(null);

        mockMvc.perform(post("/auth/auto-login")
                .cookie(longTermCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLoginException() throws Exception {
        String longTermToken = "validLongTermToken";
        Cookie longTermCookie = new Cookie("longTermCookie", longTermToken);

        when(jwtUtil.isTokenExpired(longTermToken)).thenReturn(false);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken(longTermToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(user.getId().toHexString());
        when(userService.getById(any(ObjectId.class))).thenThrow(new Exception());

        mockMvc.perform(post("/auth/auto-login")
                .cookie(longTermCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogoutSuccess() throws Exception {
        LogoutRequest request = new LogoutRequest(email);

        Cookie cookie = new Cookie("name", "value");
        cookie.setMaxAge(0);

        when(userService.getObjectIdByEmail(email)).thenReturn(userId);
        when(authGuard.validateUserAccess(userId, null)).thenReturn(null);
        when(cookieUtil.createCookie("longTermCookie", null, 0, "/auth"))
            .thenReturn(cookie);
        when(cookieUtil.createCookie("shortTermCookie", null, 0, "/secure"))
            .thenReturn(cookie);

        mockMvc.perform(post("/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void testLogoutNotFound() throws Exception {
        LogoutRequest request = new LogoutRequest(email);

        when(userService.getObjectIdByEmail(email)).thenThrow(new NotFoundException());

        mockMvc.perform(post("/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testLogoutAuthFailure() throws Exception {
        LogoutRequest request = new LogoutRequest(email);

        when(userService.getObjectIdByEmail(email)).thenReturn(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        
        mockMvc.perform(post("/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

}

    

