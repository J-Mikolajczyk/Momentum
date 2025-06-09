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

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) throws Exception{
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void register(RegisterRequest registerRequest) throws Exception{
        if(registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
            throw new RuntimeException("Email and password are required.");
        }

        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new Exception("409");
        }

        User user = registerRequest.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    public UserDTO login(LoginRequest loginRequest) throws Exception{

        Optional<User> existingUser = userRepository.findByEmail(loginRequest.getEmail().toLowerCase());

        if (existingUser.isEmpty()) {
            throw new NotFoundException();
        } else {
            User user = existingUser.get();
            if (passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
                return new UserDTO(user);
            } else {
                throw new BadCredentialsException("Invalid credentials.");
            }
        }
    }

    public UserDTO refresh(ObjectId id) throws Exception{

        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            logger.error("Refresh unsuccessful for user: {}.", id);
            throw new RuntimeException("User not found.");
        } else {
            User user = existingUser.get();
            logger.info("Refresh successful for user: {}", id);
            return new UserDTO(user);
        }
    }

    public boolean exists(UserRequest userRequest) throws Exception{
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail().toLowerCase());

        if (existingUser.isEmpty()) {
            throw new NotFoundException();
        }
        
        return true;
    }

    public User getByUserRequest(UserRequest userRequest) throws Exception{

        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());

        if (existingUser.isEmpty()) {
            throw new NotFoundException();
        }

        return existingUser.get();
    }

    public User getById(ObjectId userId) throws Exception{

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            throw new NotFoundException();
        }

        return existingUser.get();
    }

    public void addBlock(TrainingBlock block) throws Exception{

        ObjectId userId = block.getCreatedByUserID();

        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            throw new NotFoundException();
        }

        User user = existingUser.get();

        try {
            user.addBlock(block.getName());
            userRepository.save(existingUser.get());
        } catch (Exception e) {
            throw e;
        }
    }

}
