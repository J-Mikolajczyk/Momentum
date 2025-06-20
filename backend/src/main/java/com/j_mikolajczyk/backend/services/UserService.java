package com.j_mikolajczyk.backend.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.UserRepository;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TrainingBlockService blockService;

    @Autowired
    public UserService(UserRepository userRepository, TrainingBlockService blockService, BCryptPasswordEncoder passwordEncoder) throws Exception{
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blockService = blockService;
    }

    public void save(User user) {
        logger.info("Saving user '{}'", user.getEmail());
        userRepository.save(user);
    }

    public void register(RegisterRequest registerRequest) throws Exception {

        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            logger.warn("Registration failed: email '{}' already exists", registerRequest.getEmail());
            throw new Exception("409");
        }

        User user = registerRequest.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        logger.info("User registered with email '{}'", user.getEmail());
    }

    public UserDTO login(LoginRequest loginRequest) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(loginRequest.getEmail().toLowerCase());

        if (existingUser.isEmpty()) {
            logger.warn("Login failed: email '{}' not found", loginRequest.getEmail());
            throw new NotFoundException();
        } else {
            User user = existingUser.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.info("Login successful for user '{}'", loginRequest.getEmail());
                return new UserDTO(user);
            } else {
                logger.warn("Login failed: invalid credentials for '{}'", loginRequest.getEmail());
                throw new BadCredentialsException("Invalid credentials.");
            }
        }
    }

    public UserDTO refresh(ObjectId id) throws Exception {
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            logger.error("Refresh unsuccessful: user '{}' not found", id);
            throw new NotFoundException();
        } else {
            logger.info("Refresh successful for user '{}'", id);
            return new UserDTO(existingUser.get());
        }
    }

    public boolean exists(UserRequest userRequest) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail().toLowerCase());

        if (existingUser.isEmpty()) {
            logger.warn("User existence check failed: '{}' not found", userRequest.getEmail());
            throw new NotFoundException();
        }

        logger.info("User '{}' exists", userRequest.getEmail());
        return true;
    }

    public User getByUserRequest(UserRequest userRequest) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());

        if (existingUser.isEmpty()) {
            logger.warn("User fetch failed: '{}' not found", userRequest.getEmail());
            throw new NotFoundException();
        }

        logger.info("User fetched by email '{}'", userRequest.getEmail());
        return existingUser.get();
    }

    public User getById(ObjectId userId) throws Exception {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            logger.warn("User fetch failed: ID '{}' not found", userId);
            throw new NotFoundException();
        }

        logger.info("User fetched by ID '{}'", userId);
        return existingUser.get();
    }

    public void addBlock(TrainingBlock block) throws Exception {
        ObjectId userId = block.getCreatedByUserID();

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            logger.warn("Add block failed: user '{}' not found", userId);
            throw new NotFoundException();
        }

        User user = existingUser.get();

        try {
            user.addBlock(block.getName());
            userRepository.save(user);
            logger.info("Block '{}' added to user '{}'", block.getName(), userId);
        } catch (Exception e) {
            logger.error("Error adding block '{}' to user '{}': {}", block.getName(), userId, e.getMessage());
            throw e;
        }
    }

    public ObjectId getObjectIdByEmail(String email) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            logger.warn("Get ObjectId failed: user '{}' not found", email);
            throw new NotFoundException();
        }

        logger.info("ObjectId retrieved for user '{}'", email);
        return existingUser.get().getId();
    }

    public void delete(ObjectId id, String password) throws Exception {

        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            logger.warn("Delete failed: user '{}' not found", id);
            throw new NotFoundException();
        }

        User user = existingUser.get();

        if (passwordEncoder.matches(password, user.getPassword())) {
            userRepository.deleteById(id);
            blockService.deleteAllForUser(id);
            logger.info("User '{}' deleted successfully", id);
        } else {
            logger.warn("Delete failed: invalid password for user '{}'", id);
            throw new BadCredentialsException("Invalid credentials.");
        }
    }
}
