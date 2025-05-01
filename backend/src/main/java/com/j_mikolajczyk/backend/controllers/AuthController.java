package com.j_mikolajczyk.backend.controllers;

import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RefreshRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
import com.j_mikolajczyk.backend.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/exists")
    public ResponseEntity<?> userExists(@RequestBody UserRequest userRequest){
        String email = userRequest.getEmail().toLowerCase();
        System.out.println("Existence requested for user: " + email);
        try {
            userService.exists(userRequest);
            System.out.println(email + " found, returning true");
            return ResponseEntity.ok("{\"exists\": true}");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(email + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            }
            System.out.println(email + " Existence check unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        String email = registerRequest.getEmail().toLowerCase();
        System.out.println("Registration requested for user: " + email);
        try {
            userService.register(registerRequest);
            System.out.println("Registration successful for user: " + email);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
        } catch (Exception e) {
            if (e.getMessage().equals("409")) {
                System.out.println(email + " already registered.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already registered");
            }
            System.out.println(email + " registration unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        String email = loginRequest.getEmail().toLowerCase();
        System.out.println("Login requested for user: " + email);
        try {
            UserDTO userDTO = userService.login(loginRequest);
            System.out.println("User found, returning: " + email);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(email + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            } else if (e instanceof BadCredentialsException) {
                System.out.println(email + " invalid credentials, returning unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            System.out.println(email + " login unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
