package com.j_mikolajczyk.backend.controllers;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
import com.j_mikolajczyk.backend.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/exists")
    public ResponseEntity<?> userExists(@RequestBody UserRequest userRequest){
        System.out.println(userRequest);
        try {
            userService.exists(userRequest);
            System.out.println(userRequest.getEmail() + " Found, returning 200 OK");
            return ResponseEntity.ok("User exists");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(userRequest.getEmail() + " Not Found, returning 404 Not Found");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        try {
            userService.register(registerRequest);
            return ResponseEntity.ok("Registration successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            userService.login(loginRequest);
            return ResponseEntity.ok("Login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
