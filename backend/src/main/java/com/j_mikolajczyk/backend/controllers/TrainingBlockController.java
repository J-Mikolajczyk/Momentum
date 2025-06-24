package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.j_mikolajczyk.backend.dto.BlockDTO;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.block.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.block.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.block.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.block.UpdateBlockRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;
import com.j_mikolajczyk.backend.services.UserService;
import com.j_mikolajczyk.backend.utils.AuthGuard;
import com.j_mikolajczyk.backend.utils.CookieUtil;
import com.j_mikolajczyk.backend.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/secure/block")
public class TrainingBlockController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingBlockController.class);

    private final TrainingBlockService blockService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final AuthGuard authGuard;

    @Autowired
    public TrainingBlockController(TrainingBlockService blockService, UserService userService, JwtUtil jwtUtil, CookieUtil cookieUtil, AuthGuard authGuard) {
        this.blockService = blockService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.cookieUtil = cookieUtil;
        this.authGuard = authGuard;
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam("blockName") String name, @RequestParam("userId") String stringId, HttpServletRequest request) {
        ObjectId id = new ObjectId(stringId);
        logger.info("Received GET request for block '{}' from user '{}'", name, stringId);
        
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(id, request);
        if (authResponse != null) return authResponse;

        try {
            TrainingBlock block = blockService.get(name, id);
            return ResponseEntity.ok(new BlockDTO(block));
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createTrainingBlockRequest, HttpServletRequest request) {
        ObjectId userId = createTrainingBlockRequest.getUserId();
        String blockName = createTrainingBlockRequest.getBlockName();
        logger.info("Received POST request for creating block '{}' from user '{}'", blockName, userId);
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            blockService.create(createTrainingBlockRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creation successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            if ("409".equals(message)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateBlockRequest updateBlockRequest, HttpServletRequest request) throws Exception {
        ObjectId blockId = updateBlockRequest.getId();
        TrainingBlock block = blockService.get(blockId);
        ObjectId userId = block.getCreatedByUserID();
        logger.info("Received POST request for updating block '{}' from user '{}'", blockId, userId);
        
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            blockService.update(updateBlockRequest);
            return ResponseEntity.ok("Update successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/log")
    public ResponseEntity<?> log(@RequestBody UpdateBlockRequest updateBlockRequest, HttpServletRequest request) throws Exception {
        ObjectId blockId = updateBlockRequest.getId();
        TrainingBlock block = blockService.get(blockId);
        logger.info("Received POST request to log block '{}' from user '{}'", blockId, block.getCreatedByUserID());
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(block.getCreatedByUserID(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.log(updateBlockRequest);
            return ResponseEntity.ok("Update successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TrainingBlockRequest deleteBlockRequest, HttpServletRequest request) throws Exception {
        String blockName = deleteBlockRequest.getBlockName();
        ObjectId userId = deleteBlockRequest.getUserId();
        logger.info("Received POST request to delete block '{}' from user '{}'", blockName, userId);
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            blockService.delete(deleteBlockRequest);
            return ResponseEntity.ok("Delete successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) { 
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<?> rename(@RequestBody RenameBlockRequest renameBlockRequest, HttpServletRequest request) throws Exception {
        String blockName = renameBlockRequest.getBlockName();
        ObjectId userId = renameBlockRequest.getUserId();
        logger.info("Received POST request to rename block '{}' from user '{}",  blockName, userId);
        ResponseEntity<?> authResponse = authGuard.validateUserAccess(renameBlockRequest.getUserId(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.rename(renameBlockRequest);
            return ResponseEntity.ok("Rename successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if(e instanceof NotFoundException) {
                return ResponseEntity.ok("{\"exists\": false}");
            }
            if ("409".equals(message)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            return ResponseEntity.badRequest().body(message);
        }
    }
}
