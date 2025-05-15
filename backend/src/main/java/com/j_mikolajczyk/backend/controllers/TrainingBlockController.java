package com.j_mikolajczyk.backend.controllers;

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

import com.j_mikolajczyk.backend.dto.BlockDTO;
import com.j_mikolajczyk.backend.models.Day;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.requests.UpdateBlockRequest;
import com.j_mikolajczyk.backend.requests.AddWeekRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.DeleteBlockRequest;
import com.j_mikolajczyk.backend.requests.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.UpdateBlockIndexRequest;
import com.j_mikolajczyk.backend.services.TrainingBlockService;

@RestController
@RequestMapping("/secure/block")
public class TrainingBlockController {

    private final TrainingBlockService blockService;

    @Autowired
    public TrainingBlockController(TrainingBlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam("blockName") String name, @RequestParam("userId") String stringId){
        ObjectId id = new ObjectId(stringId);
        System.out.println("Block " + name + " requested for user: " + id);
        try {
            TrainingBlock block = blockService.get(name, id);
            System.out.println("Block " + name + " found for user: " + id);
            BlockDTO blockDTO = new BlockDTO(block);
            return ResponseEntity.ok(blockDTO);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(name + " or " + stringId + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
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
                System.out.println(userId + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            } else if (e.getMessage().equals("409")) {
                System.out.println(name + " block name already exists.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Blocks cannot have identical names");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateBlockRequest updateBlockRequest) throws Exception{
        System.out.println("Block update requested for block: " + updateBlockRequest.getName());
        
        String name = updateBlockRequest.getName();
        try {
            blockService.update(updateBlockRequest);
            System.out.println("Block update successful for block " + name + " new week index is: " + updateBlockRequest.getWeekIndex() + " new day index is: " + updateBlockRequest.getDayIndex());
            return ResponseEntity.status(HttpStatus.OK).body("Update successful");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(name + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteBlockRequest deleteBlockRequest) throws Exception{
        String name = deleteBlockRequest.getBlockName();
        System.out.println("Block deletion requested for block: " + name);

        try {
            blockService.delete(deleteBlockRequest);
            System.out.println("Block deletion successful for block " + name);
            return ResponseEntity.status(HttpStatus.OK).body("Update successful");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(name + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<?> rename(@RequestBody RenameBlockRequest renameBlockRequest) throws Exception{
        String name = renameBlockRequest.getBlockName();
        System.out.println("Block rename requested for block: " + name);

        try {
            blockService.rename(renameBlockRequest);
            System.out.println("Block rename successful for block " + name);
            return ResponseEntity.status(HttpStatus.OK).body("Update successful");
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                System.out.println(name + " not found, returning false");
                return ResponseEntity.ok("{\"exists\": false}");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
