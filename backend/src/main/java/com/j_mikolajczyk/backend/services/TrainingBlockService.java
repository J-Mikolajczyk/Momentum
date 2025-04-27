package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.UserRequest;
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

    public TrainingBlock get(String blockName, String email){
        if(blockName == null || email == null) {
            throw new RuntimeException("Email and Block Name are required.");
        }

        UserRequest userRequest = new UserRequest(email);
        ObjectId userId = null;

        try {
            User user = userService.get(userRequest);
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
}
