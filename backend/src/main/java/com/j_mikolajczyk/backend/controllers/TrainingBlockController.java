package com.j_mikolajczyk.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;

@RestController
@RequestMapping("/block")
public class TrainingBlockController {

    private final TrainingBlockService blockService;

    @Autowired
    public TrainingBlockController(TrainingBlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping("/get")
    public ResponseEntity<?> get(@RequestBody TrainingBlockRequest blockRequest){
        String blockId = blockRequest.getBlockId().toString();
        String userId = blockRequest.getUserId().toString();
        System.out.println("Block " + blockId + " requested for user: " + userId);
        try {
            TrainingBlock block = blockService.get(blockRequest);
            System.out.println("Block " + blockId + " found for user: " + userId);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(blockId + " not found, returning 404 Not Found");
                return ResponseEntity.notFound().build();
            }
            System.out.println(blockId + " search unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createBlockRequest){
        String userId = createBlockRequest.getUserId().toString();
        System.out.println("Block creation requested from user: " + userId);
        try {
            blockService.create(createBlockRequest);
            System.out.println("Block creation successful for user: " + userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creation successful");
        } catch (Exception e) {
            System.out.println(e);
            if (e instanceof NotFoundException) {
                System.out.println(userId + " not found, returning 404 Not Found");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
