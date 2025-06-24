package com.j_mikolajczyk.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.block.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.block.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.block.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.block.UpdateBlockRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;
import com.j_mikolajczyk.backend.utils.CookieUtil;
import com.j_mikolajczyk.backend.utils.JwtUtil;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TrainingBlockController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TestTrainingBlockController {

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

    private ObjectId userId;
    private ObjectId blockId;
    private String blockName;
    private String jwt;
    private TrainingBlock mockBlock;

    @BeforeEach
    void setUp() {
        userId = new ObjectId();
        blockId = new ObjectId();
        blockName = "testBlock";
        jwt = "mocked.jwt.token";
        mockBlock = new TrainingBlock(blockName, userId, new ArrayList<>(), false);
        mockBlock.setId(blockId);
    }

    @Test
    void testGetSuccess() throws Exception {
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
    public void testCreateSuccess() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(blockService).create(eq(request));

        mockMvc.perform(post("/secure/block/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isCreated());
    }

    @Test
    public void testCreateAuthFailure() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(any(ObjectId.class), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        doNothing().when(blockService).create(eq(request));

        mockMvc.perform(post("/secure/block/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateNotFound() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new NotFoundException()).when(blockService).create(any(CreateTrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    public void testCreateConflict() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class))).thenReturn(null);
        doThrow(new RuntimeException("409")).when(blockService).create(any(CreateTrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateException() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("duplicate", userId, new ArrayList<>());

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class))).thenReturn(null);
        doThrow(new RuntimeException()).when(blockService).create(any(CreateTrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSuccess() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(blockService).update(eq(request));

        mockMvc.perform(post("/secure/block/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateAuthFailure() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(any(ObjectId.class), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        doNothing().when(blockService).update(eq(request));

        mockMvc.perform(post("/secure/block/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateNotFound() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new NotFoundException()).when(blockService).update(any(UpdateBlockRequest.class));

        mockMvc.perform(post("/secure/block/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    public void testUpdateException() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new Exception()).when(blockService).update(any(UpdateBlockRequest.class));

        mockMvc.perform(post("/secure/block/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testLogSuccess() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(blockService).log(eq(request));

        mockMvc.perform(post("/secure/block/log")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk());
    }

    @Test
    void testLogAuthFailure() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(any(ObjectId.class), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        doNothing().when(blockService).log(eq(request));

        mockMvc.perform(post("/secure/block/log")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogNotFound() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new NotFoundException()).when(blockService).log(any(UpdateBlockRequest.class));

        mockMvc.perform(post("/secure/block/log")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    public void testLogException() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "testBlockName", new ArrayList<>(), 0, 0);

        when(blockService.get(any(ObjectId.class)))
            .thenReturn(mockBlock);
        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new Exception()).when(blockService).log(any(UpdateBlockRequest.class));

        mockMvc.perform(post("/secure/block/log")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteSuccess() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("blockName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(blockService).delete(eq(request));

        mockMvc.perform(post("/secure/block/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteAuthFailure() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("blockName", userId);

        when(authGuard.validateUserAccess(any(ObjectId.class), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        doNothing().when(blockService).delete(eq(request));

        mockMvc.perform(post("/secure/block/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteNotFound() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("blockName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new NotFoundException()).when(blockService).delete(any(TrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testDeleteException() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("blockName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new Exception()).when(blockService).delete(any(TrainingBlockRequest.class));

        mockMvc.perform(post("/secure/block/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRenameSuccess() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("blockName", "newName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doNothing().when(blockService).rename(eq(request));

        mockMvc.perform(post("/secure/block/rename")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk());
    }

    @Test
    void testRenameAuthFailure() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("blockName", "newName", userId);

        when(authGuard.validateUserAccess(any(ObjectId.class), any(HttpServletRequest.class)))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found"));
        doNothing().when(blockService).rename(eq(request));

        mockMvc.perform(post("/secure/block/rename")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testRenameNotFound() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("blockName", "newName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new NotFoundException()).when(blockService).rename(any(RenameBlockRequest.class));

        mockMvc.perform(post("/secure/block/rename")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"exists\": false}"));
    }

    @Test
    void testRenameConflict() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("blockName", "newName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new RuntimeException("409")).when(blockService).rename(any(RenameBlockRequest.class));

        mockMvc.perform(post("/secure/block/rename")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isConflict());
    }

    @Test
    void testRenameException() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("blockName", "newName", userId);

        when(authGuard.validateUserAccess(eq(userId), any(HttpServletRequest.class)))
            .thenReturn(null);
        doThrow(new Exception()).when(blockService).rename(any(RenameBlockRequest.class));

        mockMvc.perform(post("/secure/block/rename")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .cookie(new Cookie("jwt", jwt)))
            .andExpect(status().isBadRequest());
    }
}
