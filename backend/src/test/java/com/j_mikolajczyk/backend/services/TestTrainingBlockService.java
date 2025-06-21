package com.j_mikolajczyk.backend.services;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.UpdateBlockRequest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import java.beans.Transient;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestTrainingBlockService {

    @InjectMocks
    private TrainingBlockService trainingBlockService;

    @Mock
    private TrainingBlockRepository blockRepository;

    @Mock
    private UserService userService;

    private final ObjectId userId = new ObjectId();
    private final ObjectId blockId = new ObjectId();

    private User mockUser;
    private TrainingBlock mockBlock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(userId);

        mockBlock = new TrainingBlock("Test Block", userId, new ArrayList<>(), false);
        mockBlock.setId(blockId);
    }

    @Test
    void testGetByNameAndUserIdSuccess() throws Exception {
        when(userService.getById(userId)).thenReturn(mockUser);
        when(blockRepository.findByNameAndCreatedByUserID("Test Block", userId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(mockBlock.getCreatedByUserID())).thenReturn(mockUser);

        TrainingBlock result = trainingBlockService.get("Test Block", userId);

        assertEquals("Test Block", result.getName());
        verify(userService, times(2)).getById(userId);
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(blockRepository.findById(blockId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(userId)).thenReturn(mockUser);

        TrainingBlock result = trainingBlockService.get(blockId);
        assertEquals("Test Block", result.getName());
    }

    @Test
    void testCreateSuccess() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("New Block", userId, new ArrayList<>());

        doNothing().when(userService).addBlock(any(TrainingBlock.class)); 

        trainingBlockService.create(request);

        verify(blockRepository).save(any(TrainingBlock.class));
        verify(userService).addBlock(any(TrainingBlock.class));
    }

    @Test
    void testCreateThrowsException() throws Exception {
        CreateTrainingBlockRequest request = new CreateTrainingBlockRequest("New Block", userId, new ArrayList<>());

        doNothing().when(userService).addBlock(any(TrainingBlock.class)); 
        when(blockRepository.save(any(TrainingBlock.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainingBlockService.create(request));
    }


    @Test
    void testUpdateSuccess() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "Updated Block", new ArrayList<>(), 1, 2);

        when(blockRepository.findById(blockId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(userId)).thenReturn(mockUser);

        trainingBlockService.update(request);

        verify(blockRepository).save(mockBlock);
        verify(userService, times(2)).save(mockUser);
    }

    @Test
    void testUpdateException() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "Updated Block", new ArrayList<>(), 1, 2);

        when(blockRepository.findById(blockId)).thenReturn(Optional.of(mockBlock));
        when(blockRepository.save(mockBlock)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainingBlockService.update(request));
    }

    @Test
    void testLogSuccess() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "Logged Block", new ArrayList<>(), 0, 1);

        when(blockRepository.findById(blockId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(userId)).thenReturn(mockUser);

        trainingBlockService.log(request);

        assertTrue(mockBlock.getLogged());
        verify(blockRepository).save(mockBlock);
    }

    @Test
    void testLogException() throws Exception {
        UpdateBlockRequest request = new UpdateBlockRequest(blockId.toHexString(), "Logged Block", new ArrayList<>(), 0, 1);

        when(blockRepository.findById(blockId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(userId)).thenThrow(new NotFoundException());

        assertThrows(NotFoundException.class, () -> trainingBlockService.log(request));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("Test Block", userId);

        when(userService.getById(userId)).thenReturn(mockUser);
        when(blockRepository.findByNameAndCreatedByUserID("Test Block", userId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(mockBlock.getCreatedByUserID())).thenReturn(mockUser);

        trainingBlockService.delete(request);

        verify(blockRepository).delete(mockBlock);
    }

    @Test
    void testDeleteException() throws Exception {
        TrainingBlockRequest request = new TrainingBlockRequest("Test Block", userId);

        when(userService.getById(userId)).thenThrow(new NotFoundException());

        assertThrows(NotFoundException.class, () -> trainingBlockService.delete(request));
    }

    @Test
    void testRenameSuccess() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("Test Block", "New Block Name", userId);

        mockUser.setTrainingBlockNames(new ArrayList<>(List.of("Test Block")));

        when(userService.getById(userId)).thenReturn(mockUser);
        when(blockRepository.findByNameAndCreatedByUserID("Test Block", userId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(mockBlock.getCreatedByUserID())).thenReturn(mockUser);

        trainingBlockService.rename(request);

        verify(blockRepository).save(mockBlock);
        verify(userService, times(2)).save(mockUser);
    }

    @Test
    void testRenameConflict() throws Exception {
        RenameBlockRequest request = new RenameBlockRequest("Test Block", "Same Block Name", userId);

        List<String> blockNames = new ArrayList<>();
        blockNames.add("Test Block");
        blockNames.add("Same Block Name");
        mockUser.setTrainingBlockNames(blockNames);

        when(userService.getById(userId)).thenReturn(mockUser);
        when(blockRepository.findByNameAndCreatedByUserID("Test Block", userId)).thenReturn(Optional.of(mockBlock));
        when(userService.getById(mockBlock.getCreatedByUserID())).thenReturn(mockUser);

        assertThrows(Exception.class, () -> trainingBlockService.rename(request));
    }

    @Test
    void testDeleteAllForUser() {
        trainingBlockService.deleteAllForUser(userId);
        verify(blockRepository).deleteByCreatedByUserID(userId);
    }

    @Test
    void testGetThrowsExceptionWhenBlockNotFound() {
        when(blockRepository.findById(blockId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> trainingBlockService.get(blockId));
    }

    @Test
    void testGetWithNulls() {
        assertThrows(RuntimeException.class, () -> trainingBlockService.get(null, null));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(userService.getById(userId)).thenThrow(new NotFoundException());

        assertThrows(NotFoundException.class, () -> trainingBlockService.get("Test Block", userId));
    }

    @Test
    void testGetBlockNotFound() throws Exception {
        when(userService.getById(userId)).thenReturn(mockUser);

        when(blockRepository.findByNameAndCreatedByUserID(null, null)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingBlockService.get("Test Block", userId));
    }

}
