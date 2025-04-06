package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.UserRepository;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void register(RegisterRequest registerRequest){
        if(registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
            throw new RuntimeException("Email and password are required.");
        }

        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }

        User user = registerRequest.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    public User login(LoginRequest loginRequest){

        Optional<User> existingUser = userRepository.findByEmail(loginRequest.getEmail());

        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        return existingUser.get();
    }

    public boolean exists(UserRequest userRequest){

        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());

        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        return true;
    }
}
