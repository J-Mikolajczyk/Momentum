package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/secure/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthGuard authGuard;


    @Autowired
    public UserController(UserService userService, AuthGuard authGuard) {
        this.userService = userService;
        this.authGuard = authGuard;
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> login(@RequestParam("userId") String stringId, HttpServletRequest request){
        logger.info("Received GET request for user '{}'", stringId);
        ObjectId id = new ObjectId(stringId);
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(id, request);
        if (authResponse != null) return authResponse;
        try {
            UserDTO userDTO = userService.refresh(id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody LoginRequest deleteUserRequest, HttpServletRequest request){
        String email = deleteUserRequest.getEmail().toLowerCase();
        logger.info("Received POST request to delete user '{}'", email);
        ObjectId userId = null;
        try {
            userId = userService.getObjectIdByEmail(email);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"exists\": false}");
        }

        logger.info("{}", deleteUserRequest.getPassword());

        ResponseEntity<?> authResponse = authGuard.validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;
        try {
            userService.delete(userId, deleteUserRequest.getPassword());
            return ResponseEntity.ok("{\"deleted\": true}");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            if (e instanceof BadCredentialsException) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
