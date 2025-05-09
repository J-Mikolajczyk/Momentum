package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.dto.UserDTO;
import com.j_mikolajczyk.backend.services.UserService;

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
}
