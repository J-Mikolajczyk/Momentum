package com.j_mikolajczyk.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            TrainingBlock block = blockService.get(blockRequest);
            return ResponseEntity.ok(block);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTrainingBlockRequest createBlockRequest){
        try {
            blockService.create(createBlockRequest);
            return ResponseEntity.ok("Creation successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
