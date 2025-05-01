package com.j_mikolajczyk.backend.controllers;

import org.apache.coyote.BadRequestException;
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
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.requests.LoginRequest;
import com.j_mikolajczyk.backend.requests.RefreshRequest;
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
}
