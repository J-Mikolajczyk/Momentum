package com.j_mikolajczyk.backend.utils;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthGuard {

    private static final Logger logger = LoggerFactory.getLogger(AuthGuard.class);

    private final UserService userService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthGuard(UserService userService, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.cookieUtil = cookieUtil;
    }

    public ResponseEntity<?> validateUserAccess(ObjectId userId, HttpServletRequest request) {
        User user;
        try {
            user = userService.getById(userId);
        } catch (Exception e) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        String jwt = cookieUtil.extractShortTermJwt(request);
        if (jwt == null) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, "No cookies found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT cookie found");
        }

        String email;
        try {
            email = jwtUtil.extractEmail(jwt);
        } catch (Exception e) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, "Invalid JWT.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT");
        }

        if(email == null) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, "No subject found in JWT.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No subject found in JWT.");
        }

        if (!email.equals(user.getEmail())) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, "Wrong user's cookie.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user's cookie");
        }

        return null;
    }
    
}
