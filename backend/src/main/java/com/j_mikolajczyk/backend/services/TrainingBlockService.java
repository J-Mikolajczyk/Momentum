package com.j_mikolajczyk.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;

@Service
public class TrainingBlockService {

    private final TrainingBlockRepository blockRepository;

    @Autowired
    public TrainingBlockService(TrainingBlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public TrainingBlock get(TrainingBlockRequest blockRequest){
        if(blockRequest.getUserId() == null || blockRequest.getBlockId() == null) {
            throw new RuntimeException("UserID and BlockID are required.");
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findByIdAndCreatedByUserID(blockRequest.getBlockId(), blockRequest.getUserId());

        if (fetchedBlock.isPresent()) {
            return fetchedBlock.get();
        } else {
            throw new RuntimeException("Block not found.");
        }
    }

    public void create(CreateTrainingBlockRequest createBlockRequest){
        if(createBlockRequest.getUserId() == null) {
            throw new RuntimeException("UserID is required.");
        }

        TrainingBlock block = new TrainingBlock(createBlockRequest.getName(), createBlockRequest.getUserId());

        blockRepository.save(block);
    }
}
