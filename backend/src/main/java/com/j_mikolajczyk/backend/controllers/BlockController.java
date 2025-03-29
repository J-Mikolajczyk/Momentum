package com.j_mikolajczyk.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.BlockRequest;
import com.j_mikolajczyk.backend.requests.CreateBlockRequest;
import com.j_mikolajczyk.backend.services.BlockService;

@RestController
@RequestMapping("/block")
public class BlockController {

    private final BlockService blockService;

    @Autowired
    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @PostMapping("/get")
    public ResponseEntity<?> get(@RequestBody BlockRequest blockRequest){
        try {
            TrainingBlock block = blockService.get(blockRequest);
            return ResponseEntity.ok(block);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateBlockRequest createBlockRequest){
        try {
            blockService.create(createBlockRequest);
            return ResponseEntity.ok("Creation successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
