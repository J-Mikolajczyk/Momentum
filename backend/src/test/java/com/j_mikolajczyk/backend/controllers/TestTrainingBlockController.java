package com.j_mikolajczyk.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.block.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;
import com.j_mikolajczyk.backend.utils.CookieUtil;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TrainingBlockController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TestTrainingBlockController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingBlockService blockService;

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

    @Test
    void testGetSuccess() throws Exception {
        ObjectId userId = new ObjectId();
        String blockName = "testBlock";
        String jwt = "mocked.jwt.token";

        TrainingBlock mockBlock = new TrainingBlock();
        mockBlock.setId(new ObjectId());
        mockBlock.setCreatedByUserID(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(blockService.get(eq(blockName), eq(userId)))
            .thenReturn(mockBlock);

        mockMvc.perform(get("/secure/block/get")
                .param("blockName", blockName)
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAuthFailure() throws Exception {
        ObjectId userId = new ObjectId();
        String blockName = "testBlock";
        String jwt = "mocked.jwt.token";

        TrainingBlock mockBlock = new TrainingBlock();
        mockBlock.setId(new ObjectId());
        mockBlock.setCreatedByUserID(userId);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        mockMvc.perform(get("/secure/block/get")
                .param("blockName", blockName)
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotFound() throws Exception {
        ObjectId userId = new ObjectId();
        String blockName = "testBlock";
        String jwt = "mocked.jwt.token";

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(blockService.get(eq(blockName), eq(userId)))
            .thenThrow(new NotFoundException());

        mockMvc.perform(get("/secure/block/get")
                .param("blockName", blockName)
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testGetBadRequest() throws Exception {
        ObjectId userId = new ObjectId();
        String blockName = "testBlock";
        String jwt = "mocked.jwt.token";

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        when(blockService.get(eq(blockName), eq(userId)))
            .thenThrow(new Exception());

        mockMvc.perform(get("/secure/block/get")
                .param("blockName", blockName)
                .param("userId", userId.toString())
                .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateConflict() throws Exception {
        ObjectId userId = new ObjectId();
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class))).thenReturn(null);
        doThrow(new RuntimeException("409")).when(blockService).create(any(CreateTrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Blocks cannot have identical names"));
    }
}
