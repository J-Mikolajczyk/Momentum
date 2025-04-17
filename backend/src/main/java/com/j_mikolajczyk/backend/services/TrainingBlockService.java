package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
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

    public TrainingBlock get(TrainingBlockRequest blockRequest){
        if(blockRequest.getUserId() == null || blockRequest.getName() == null) {
            throw new RuntimeException("UserID and Block Name are required.");
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findByNameAndCreatedByUserID(blockRequest.getName(), blockRequest.getUserId());

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
