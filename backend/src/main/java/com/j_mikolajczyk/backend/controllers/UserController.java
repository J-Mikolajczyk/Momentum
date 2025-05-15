package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.requests.LogoutRequest;
import com.j_mikolajczyk.backend.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/secure/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> login(@RequestParam("userId") String stringId){
        ObjectId id = new ObjectId(stringId);
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
}
