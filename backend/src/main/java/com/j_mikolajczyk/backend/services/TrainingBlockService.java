package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.models.Week;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
import com.j_mikolajczyk.backend.requests.AddWeekRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;

@Service
public class TrainingBlockService {

    private final TrainingBlockRepository blockRepository;
    private final UserService userService;

    @Autowired
    public TrainingBlockService(TrainingBlockRepository blockRepository, UserService userService) {
        this.blockRepository = blockRepository;
        this.userService = userService;
    }

    public TrainingBlock get(String blockName, ObjectId id){
        if(blockName == null || id == null) {
            throw new RuntimeException("Email and Block Name are required.");
        }

        ObjectId userId = null;

        try {
            User user = userService.getById(id);
            userId = user.getId();
        } catch(Exception e) {
            throw new RuntimeException("User not found.");
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findByNameAndCreatedByUserID(blockName, userId);

        if (fetchedBlock.isPresent()) {
            return fetchedBlock.get();
        } else {
            throw new RuntimeException("Block not found.");
        }
    }

    public void create(CreateTrainingBlockRequest createBlockRequest) throws Exception{

        if(createBlockRequest.getUserId() == null) {
            throw new RuntimeException("UserID is required.");
        }

        TrainingBlock block = new TrainingBlock(createBlockRequest.getBlockName(), createBlockRequest.getUserId());

        try {
            blockRepository.save(block);
            userService.addBlock(block);
        } catch (Exception e) {
            blockRepository.delete(block);
            throw e;
        }

    }

    public void addWeek(AddWeekRequest addWeekRequest) throws Exception{
        ObjectId id = addWeekRequest.getUserId();
        String blockName = addWeekRequest.getBlockName();

        if(id == null || blockName == null) {
            throw new RuntimeException("UserID and Block Name is required.");
        }

        Week week = new Week();

        try {
            TrainingBlock block = this.get(blockName, id);
            block.addWeek(week);
            blockRepository.save(block);
        } catch (Exception e) {
            throw e;
        }

    }
}
