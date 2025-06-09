package com.j_mikolajczyk.backend.controllers;

import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.LogoutRequest;
import com.j_mikolajczyk.backend.requests.RegisterRequest;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.CookieUtil;
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
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        String email = registerRequest.getEmail().toLowerCase();
        logger.info("Registration attempt for email: {}", email);
        try {
            userService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
        } catch (Exception e) {
            if (e.getMessage().equals("409")){
                logger.warn("Registration failed for email: {}. User not found.", email);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already registered");
            }
            logger.error("Registration failed for email: {}, reason: {}", email, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail().toLowerCase();
        logger.info("Login attempt for email: {}", email);
        try {
            UserDTO userDTO = userService.login(loginRequest);
            Map<String, String> tokens = jwtUtil.generateJwtToken(userDTO);
            
            Cookie longTermCookie = cookieUtil.createCookie("longTermCookie", tokens.get("longTermToken"), (int) longTermExpiration / 1000 , "/auth");
            response.addCookie(longTermCookie);

            Cookie shortTermCookie = cookieUtil.createCookie("shortTermCookie", tokens.get("shortTermToken"), (int) shortTermExpiration / 1000, "/secure");
            response.addCookie(shortTermCookie);
            logger.info("Login successful for email: {}", email);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                logger.warn("Login failed - user not found: {}", email);
                return ResponseEntity.ok("{\"exists\": false}");
            } else if (e instanceof BadCredentialsException) {
                logger.warn("Login failed - bad credentials for email: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            logger.error("Login failed due to unexpected error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auto-login")
    public ResponseEntity<?> autoLogin(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Auto-login attempt with long-term cookie");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            logger.warn("Auto-login failed: missing or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing necessary cookies");
        }

        String longTermToken = null;

        for (Cookie cookie : cookies) {
            if ("longTermCookie".equals(cookie.getName())) {
                longTermToken = cookie.getValue();
            }
        }

        if (longTermToken == null) {
            logger.warn("Auto-login failed: missing or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing tokens");
        }

        if(jwtUtil.isTokenExpired(longTermToken)) {
            logger.warn("Auto-login failed: missing or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired");
        }

        Claims claims = jwtUtil.validateToken(longTermToken);
        if (claims == null) {
            logger.warn("Auto-login failed: invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        try {
            ObjectId userId = new ObjectId(claims.getSubject());
            User user = userService.getById(userId);
            UserDTO userDTO = new UserDTO(user);

            String newShortTermToken = jwtUtil.generateShortTermToken(userDTO);
            Cookie shortTermCookie = cookieUtil.createCookie("shortTermCookie", newShortTermToken, (int) shortTermExpiration / 1000, "/secure");
            response.addCookie(shortTermCookie);    
            logger.info("Auto-login successful for user ID: {}", user.getId());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.warn("Auto-login failed: invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired long-term token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request, HttpServletResponse response) {
        String email = logoutRequest.getEmail();
        logger.info("Logout requested for email: {}", email);
        ResponseEntity<?> authResponse = validateUserAccess(email, request);
        if (authResponse != null) return authResponse;
        
        Cookie longTermCookie = cookieUtil.createCookie("longTermCookie", null, 0, "/auth");
        response.addCookie(longTermCookie);

        Cookie shortTermCookie = cookieUtil.createCookie("shortTermCookie", null, 0, "/secure");
        response.addCookie(shortTermCookie);
        logger.info("Logout successful for email: {}", email);
        return ResponseEntity.ok("Logged out successfully");
    }

    private ResponseEntity<?> validateUserAccess(String givenEmail, HttpServletRequest request) {
        logger.debug("Validating JWT for email: {}", givenEmail);
        String jwt = getJwtFromCookies(request);
        if (jwt == null) {
            logger.warn("JWT validation failed: invalid token or email mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT cookie found");
        }

        String email;
        try {
            email = jwtUtil.extractEmail(jwt);
        } catch (Exception e) {
            logger.warn("JWT validation failed: invalid token or email mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT");
        }

        if(email == null) {
            logger.warn("JWT validation failed: invalid token or email mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user's cookie");
        }

        if (!email.equals(givenEmail)) {
            logger.warn("JWT validation failed: invalid token or email mismatch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong user's cookie");
        }

        return null;
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null)  {
            logger.warn("JWT validation failed: no cookie found");
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("longTermCookie".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
