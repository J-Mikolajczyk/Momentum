package com.j_mikolajczyk.backend.services;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.repositories.UserRepository;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.UpdateBlockRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;

import org.assertj.core.condition.Not;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class TestUserService {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingBlockService trainingBlockService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

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
    void testSave() throws Exception {
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        userService.save(mockUser);
        verify(userRepository).save(mockUser);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("email@example.com", "password", "John Doe");

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        User newUser = new User();
        newUser.setEmail("email@example.com");
        newUser.setPassword("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.register(request);

        verify(userRepository).findByEmail("email@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(argThat(user -> 
            user.getEmail().equals("email@example.com") && 
            user.getPassword().equals("hashedPassword")
        ));
    }

    @Test
    void testRegisterThrowsExceptionWhenUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("email@example.com", "password", "John Doe");

        when(userRepository.findByEmail("email@example.com"))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(new User()));

        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        User newUser = new User();
        newUser.setEmail("email@example.com");
        newUser.setPassword("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        userService.register(request);
        
        assertThrows(Exception.class, () -> userService.register(request));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("email@example.com", "password");

        User user = new User("email@example.com", "hashedPassword", "John Doe");
        user.setId(userId);

        when(userRepository.findByEmail("email@example.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashedPassword"))
            .thenReturn(true);

        userService.login(request);

        verify(userRepository).findByEmail("email@example.com");
        verify(passwordEncoder).matches("password", "hashedPassword");

    }

    @Test
    void testLoginNotFound() throws Exception {
        LoginRequest request = new LoginRequest("email@example.com", "password");

        when(userRepository.findByEmail("email@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.login(request));
    }

    @Test
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("email@example.com", "password");

        when(userRepository.findByEmail("email@example.com"))
            .thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches("password", "hashedPassword"))
            .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(request));
    }
    

    @Test
    void testRefreshSuccess() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.refresh(userId);

        verify(userRepository).findById(userId);
    }

    @Test
    void testRefreshNotFound() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.refresh(userId));
    }

    @Test
    void testExistsSuccess() throws Exception {
        UserRequest userRequest = new UserRequest("email@example.com");
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        userService.exists(userRequest);

        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void testExistsNotFound() throws Exception {
        UserRequest userRequest = new UserRequest("email@example.com");
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.exists(userRequest));
    }

    @Test
    void testGetByUserRequestSuccess() throws Exception {
        UserRequest userRequest = new UserRequest("email@example.com");
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        userService.getByUserRequest(userRequest);

        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void testGetByUserRequestNotFound() throws Exception {
        UserRequest userRequest = new UserRequest("email@example.com");
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getByUserRequest(userRequest));
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.getById(userId);

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void testAddBlockSuccess() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.addBlock(mockBlock);

        verify(userRepository).findById(userId);
        verify(userRepository).save(mockUser);
    }

    @Test
    void testAddBlockUserNotFound() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addBlock(mockBlock));
    }

    @Test
    void testAddBlockException() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> userService.addBlock(mockBlock));
    }

    @Test
    void testGetObjectIdByEmailSuccess() throws Exception {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        userService.getObjectIdByEmail("email@example.com");

        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void testGetObjectIdByEmailNotFound() throws Exception {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->userService.getObjectIdByEmail("email@example.com"));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        mockUser.setPassword("hashedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);

        doNothing().when(userRepository).deleteById(userId);
        doNothing().when(trainingBlockService).deleteAllForUser(userId);

        userService.delete(userId, "password");

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches("password", "hashedPassword");
        verify(userRepository).deleteById(userId);
        verify(trainingBlockService).deleteAllForUser(userId);
    }

    @Test
    void testDeleteNotFound() throws Exception { 
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(userId, "password"));
    }

    @Test
    void testDeleteBadCredentials() throws Exception {
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.delete(userId, "password"));
    }
}
