package com.j_mikolajczyk.backend.controllers;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
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
            
            Cookie longTermCookie = createCookie("longTermCookie", tokens.get("longTermToken"), (int) longTermExpiration / 1000 , "/auth");
            response.addCookie(longTermCookie);

            Cookie shortTermCookie = createCookie("shortTermCookie", tokens.get("shortTermToken"), (int) shortTermExpiration / 1000, "/secure");
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
            System.out.println("Auto-login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing necessary cookies");
        }

        String longTermToken = null;

        for (Cookie cookie : cookies) {
            if ("longTermCookie".equals(cookie.getName())) {
                longTermToken = cookie.getValue();
            }
        }

        if (longTermToken == null) {
            System.out.println("Auto-login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing tokens");
        }

        if(jwtUtil.isTokenExpired(longTermToken)) {
            System.out.println("Auto-login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired");
        }

        Claims claims = jwtUtil.validateToken(longTermToken);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        try {
            ObjectId userId = new ObjectId(claims.getSubject());
            User user = userService.getById(userId);
            UserDTO userDTO = new UserDTO(user);

            String newShortTermToken = jwtUtil.generateShortTermToken(userDTO);
            Cookie shortTermCookie = createCookie("shortTermCookie", newShortTermToken, (int) shortTermExpiration / 1000, "/secure");
            response.addCookie(shortTermCookie);    

            System.out.println("Auto-login successful for " + userDTO.getEmail());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            System.out.println("Auto-login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired long-term token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest, HttpServletResponse response) {
        String emailString = logoutRequest.getEmail();
        System.out.println("Logout requested for " + emailString + ", clearing cookies");
        Cookie longTermCookie = createCookie("longTermCookie", null, 0, "/auth");
        response.addCookie(longTermCookie);

        Cookie shortTermCookie = createCookie("shortTermCookie", null, 0, "/secure");
        response.addCookie(shortTermCookie);
        System.out.println(emailString + " logged out, cookies cleared");

        return ResponseEntity.ok("Logged out successfully");
    }

    private Cookie createCookie(String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("training-momentum.com");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }
    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.ok().build();
    }
}
