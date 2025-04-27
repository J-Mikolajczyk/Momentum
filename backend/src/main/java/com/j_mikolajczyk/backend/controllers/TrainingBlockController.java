package com.j_mikolajczyk.backend.controllers;

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

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam("blockName") String name, @RequestParam("email") String email){
        System.out.println("Block " + name + " requested for user: " + email);
        try {
            TrainingBlock block = blockService.get(name, email);
            System.out.println("Block " + name + " found for user: " + email);
            return ResponseEntity.ok(block);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(name + " not found, returning 404 Not Found");
                return ResponseEntity.notFound().build();
            }
            System.out.println(name + " search unsuccessful, returning bad request");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createBlockRequest){
        String name = createBlockRequest.getBlockName();
        String userId = createBlockRequest.getUserId().toString();
        System.out.println("Block creation requested from user: " + userId);
        try {
            blockService.create(createBlockRequest);
            System.out.println("Block creation successful for user: " + userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creation successful");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(userId + " not found, returning 404 Not Found");
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().equals("409")) {
                System.out.println(name + " block name already exists.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
