package com.j_mikolajczyk.backend.controllers;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException.NotFound;

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

    private static final Logger logger = LoggerFactory.getLogger(TrainingBlockController.class);

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
        logger.info("Fetching block '{}' for user '{}'", name, stringId);
        ResponseEntity<?> authResponse = validateUserAccess(id, request);
        if (authResponse != null) return authResponse;

        try {
            logger.info("Block '{}' found for user '{}'", name, stringId);
            TrainingBlock block = blockService.get(name, id);
            return ResponseEntity.ok(new BlockDTO(block));
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                logger.warn("Block '{}' not found for user '{}'", name, stringId);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            logger.error("Error fetching block '{}': {}", name, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createTrainingBlockRequest, HttpServletRequest request) {
        ObjectId userId = createTrainingBlockRequest.getUserId();
        String blockName = createTrainingBlockRequest.getBlockName();
        logger.info("Creating new block '{}' for user '{}'", blockName, userId);
        ResponseEntity<?> authResponse = validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            blockService.create(createTrainingBlockRequest);
            logger.info("Block '{}' created for user '{}'", blockName, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creation successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                logger.warn("Block creation failed: user '{}' not found", userId);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            if ("409".equals(message)) {
                logger.warn("Block creation conflict: block '{}' already exists for user '{}'", blockName, userId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            logger.error("Error creating block '{}': {}", blockName, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateBlockRequest updateBlockRequest, HttpServletRequest request) throws Exception {
        ObjectId blockId = updateBlockRequest.getId();
        TrainingBlock block = blockService.get(blockId);
        ObjectId userId = block.getCreatedByUserID();
        logger.info("Block update for '{}' requested from user '{}'", blockId, userId);
        
        ResponseEntity<?> authResponse = validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            logger.info("Updating block '{}' for user '{}'", blockId, userId);
            blockService.update(updateBlockRequest);
            return ResponseEntity.ok("Update successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                logger.warn("Error updating block '{}': {}. Block not found.", blockId, message);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            logger.error("Error updating block '{}': {}", blockId, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/log")
    public ResponseEntity<?> log(@RequestBody UpdateBlockRequest updateBlockRequest, HttpServletRequest request) throws Exception {
        ObjectId blockId = updateBlockRequest.getId();
        TrainingBlock block = blockService.get(blockId);
        logger.info("Log requested for block '{}' from user '{}'", blockId, block.getCreatedByUserID());
        ResponseEntity<?> authResponse = validateUserAccess(block.getCreatedByUserID(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.log(updateBlockRequest);
            logger.info("Logging block '{}' for user '{}'", blockId, block.getCreatedByUserID());
            return ResponseEntity.ok("Update successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) {
                logger.warn("Error logging to block '{}': {}. Block not found.", blockId, message);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            logger.error("Error logging to block '{}': {}", blockId, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteBlockRequest deleteBlockRequest, HttpServletRequest request) throws Exception {
        String blockName = deleteBlockRequest.getBlockName();
        ObjectId userId = deleteBlockRequest.getUserId();
        logger.info("Block deletion requested for '{}' from user '{}'", blockName, userId);
        ResponseEntity<?> authResponse = validateUserAccess(userId, request);
        if (authResponse != null) return authResponse;

        try {
            logger.info("Deleting block '{}' for user '{}'", blockName, userId);
            blockService.delete(deleteBlockRequest);
            return ResponseEntity.ok("Delete successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if (e instanceof NotFoundException) { 
                logger.warn("Error deleting block '{}': {}. Block not found.", blockName, message);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            logger.error("Error deleting block '{}': {}.", blockName, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<?> rename(@RequestBody RenameBlockRequest renameBlockRequest, HttpServletRequest request) throws Exception {
        String blockName = renameBlockRequest.getBlockName();
        ObjectId userId = renameBlockRequest.getUserId();
        logger.info("Block rename requested for '{}' from user '{}'", blockName, userId);
        ResponseEntity<?> authResponse = validateUserAccess(renameBlockRequest.getUserId(), request);
        if (authResponse != null) return authResponse;

        try {
            blockService.rename(renameBlockRequest);
            logger.info("Renamed block '{}' for user '{}'", blockName, userId);
            return ResponseEntity.ok("Rename successful");
        } catch (Exception e) {
            String message = e.getMessage();
            if(e instanceof NotFoundException) {
                logger.warn("Error renaming block '{}': {}. Block not found.", blockName, message);
                return ResponseEntity.ok("{\"exists\": false}");
            }
            if ("409".equals(message)) {
                logger.warn("Error renaming block '{}': {}. Block by that name already exists.", blockName, message);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            logger.error("Error renaming block '{}': {}", blockName, message);
            return ResponseEntity.badRequest().body(message);
        }
    }

    private ResponseEntity<?> validateUserAccess(ObjectId userId, HttpServletRequest request) {
        User user;
        try {
            user = userService.getById(userId);
        } catch (Exception e) {
            logger.warn("Unauthorized access attempt for user '{}': {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        String jwt = getJwtFromCookies(request);
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
