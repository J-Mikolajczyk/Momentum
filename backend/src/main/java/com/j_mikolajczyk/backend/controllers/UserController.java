package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.requests.LogoutRequest;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/secure/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;


    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> login(@RequestParam("userId") String stringId, HttpServletRequest request){
        ObjectId id = new ObjectId(stringId);
        ResponseEntity<?> authResponse = validateUserAccess(id, request);
        if (authResponse != null) return authResponse;

        System.out.println("Refresh requested for user: " + id);
        try {
            UserDTO userDTO = userService.refresh(id);
            System.out.println("User found, returning: " + id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(id + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            }
            System.out.println(id + " refresh unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> validateUserAccess(ObjectId userId, HttpServletRequest request) {
        User user;
        try {
            user = userService.getById(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        String jwt = getJwtFromCookies(request);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT cookie found");
        }

        String email;
        try {
            email = jwtUtil.extractEmail(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT");
        }

        if(email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user's cookie");
        }

        if (!email.equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user's cookie");
        }

        return null;
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("shortTermCookie".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
