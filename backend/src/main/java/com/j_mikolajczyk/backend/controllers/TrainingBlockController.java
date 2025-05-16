package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.j_mikolajczyk.backend.dto.BlockDTO;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.requests.*;
import com.j_mikolajczyk.backend.services.TrainingBlockService;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/secure/block")
public class TrainingBlockController {

    private final TrainingBlockService blockService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TrainingBlockController(TrainingBlockService blockService, UserService userService, JwtUtil jwtUtil) {
        this.blockService = blockService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam("blockName") String name, @RequestParam("userId") String stringId, HttpServletRequest request) {
        ObjectId id = new ObjectId(stringId);
        ResponseEntity<?> authResponse = validateUserAccess(id, request);
        if (authResponse != null) return authResponse;

        try {
            TrainingBlock block = blockService.get(name, id);
            return ResponseEntity.ok(new BlockDTO(block));
        } catch (NotFoundException e) {
            return ResponseEntity.ok("{\"exists\": false}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createTrainingBlockRequest, HttpServletRequest request) {
        ResponseEntity<?> authResponse = validateUserAccess(createTrainingBlockRequest.getUserId(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.create(createTrainingBlockRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creation successful");
        } catch (NotFoundException e) {
            return ResponseEntity.ok("{\"exists\": false}");
        } catch (Exception e) {
            if ("409".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateBlockRequest updateBlockRequest, HttpServletRequest request) throws Exception {
        TrainingBlock block = blockService.get(updateBlockRequest.getId());
        ResponseEntity<?> authResponse = validateUserAccess(block.getCreatedByUserID(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.update(updateBlockRequest);
            return ResponseEntity.ok("Update successful");
        } catch (NotFoundException e) {
            return ResponseEntity.ok("{\"exists\": false}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteBlockRequest deleteBlockRequest, HttpServletRequest request) throws Exception {
        ResponseEntity<?> authResponse = validateUserAccess(deleteBlockRequest.getUserId(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.delete(deleteBlockRequest);
            return ResponseEntity.ok("Delete successful");
        } catch (NotFoundException e) {
            return ResponseEntity.ok("{\"exists\": false}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<?> rename(@RequestBody RenameBlockRequest renameBlockRequest, HttpServletRequest request) throws Exception {
        ResponseEntity<?> authResponse = validateUserAccess(renameBlockRequest.getUserId(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.rename(renameBlockRequest);
            return ResponseEntity.ok("Rename successful");
        } catch (NotFoundException e) {
            return ResponseEntity.ok("{\"exists\": false}");
        } catch (Exception e) {
            if ("409".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
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
