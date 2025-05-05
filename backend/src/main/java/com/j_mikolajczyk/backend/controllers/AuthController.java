package com.j_mikolajczyk.backend.controllers;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.LogoutRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.longTermExpiration}")
    private long longTermExpiration;

    @Value("${jwt.shortTermExpiration}")
    private long shortTermExpiration;

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail().toLowerCase();
        System.out.println("Login requested for user: " + email);
        try {
            UserDTO userDTO = userService.login(loginRequest);
            Map<String, String> tokens = jwtUtil.generateJwtToken(userDTO);
            
            Cookie longTermCookie = new Cookie("longTermCookie", tokens.get("longTermToken"));
            longTermCookie.setHttpOnly(true);
            longTermCookie.setPath("/");
            longTermCookie.setMaxAge((int) longTermExpiration / 1000);
            longTermCookie.setSecure(true);
            longTermCookie.setAttribute("SameSite", "None");
            response.addCookie(longTermCookie);

            Cookie shortTermCookie = new Cookie("shortTermCookie", tokens.get("shortTermToken"));
            shortTermCookie.setHttpOnly(true);
            shortTermCookie.setPath("/");
            shortTermCookie.setMaxAge((int) shortTermExpiration / 1000);
            shortTermCookie.setSecure(true);
            shortTermCookie.setAttribute("SameSite", "None");
            response.addCookie(shortTermCookie);
            
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

    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found");
        }

        String longTermToken = null;
        for (Cookie cookie : cookies) {
            if ("longTermCookie".equals(cookie.getName())) {
                longTermToken = cookie.getValue();
                break;
            }
        }

        if (longTermToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing long-term token");
        }

        try {
            Claims claims = jwtUtil.validateToken(longTermToken);
            String userId = claims.getSubject();

            User user = userService.getById(new ObjectId(userId));
            UserDTO userDTO = new UserDTO(user);

            String newShortTermToken = jwtUtil.generateShortTermToken(userDTO);

            Cookie shortTermCookie = new Cookie("shortTermCookie", newShortTermToken);
            shortTermCookie.setHttpOnly(true);
            shortTermCookie.setPath("/");
            shortTermCookie.setMaxAge((int) shortTermExpiration / 1000);
            shortTermCookie.setSecure(true);
            response.addCookie(shortTermCookie);

            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired long-term token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest, HttpServletResponse response) {
        String emailString = logoutRequest.getEmail();
        System.out.println("Logout requested for " + emailString + ", clearing cookies");
        Cookie longTermCookie = new Cookie("longTermCookie", null);
        longTermCookie.setHttpOnly(true);
        longTermCookie.setPath("/");
        longTermCookie.setMaxAge(0); 
        longTermCookie.setAttribute("SameSite", "None");
        response.addCookie(longTermCookie);

        Cookie shortTermCookie = new Cookie("shortTermCookie", null);
        shortTermCookie.setHttpOnly(true);
        shortTermCookie.setPath("/");
        shortTermCookie.setMaxAge(0);
        shortTermCookie.setAttribute("SameSite", "None");
        response.addCookie(shortTermCookie);

        System.out.println(emailString + " logged out, cookies cleared");

        return ResponseEntity.ok("Logged out successfully");
    }
}
