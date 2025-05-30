package com.j_mikolajczyk.backend.services;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.j_mikolajczyk.backend.models.Day;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.models.Week;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.UpdateBlockRequest;
import com.j_mikolajczyk.backend.requests.AddWeekRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.DeleteBlockRequest;
import com.j_mikolajczyk.backend.requests.RenameBlockRequest;

@Service
public class TrainingBlockService {

    private final TrainingBlockRepository blockRepository;
    private final UserService userService;

    @Autowired
    public TrainingBlockService(TrainingBlockRepository blockRepository, UserService userService) {
        this.blockRepository = blockRepository;
        this.userService = userService;
    }

    public TrainingBlock get(String blockName, ObjectId id) throws Exception{
        if(blockName == null || id == null) {
            throw new RuntimeException("Email and Block Name are required.");
        }

        ObjectId userId = null;

        try {
            User user = userService.getById(id);
            userId = user.getId();
        } catch(Exception e) {
            throw new NotFoundException();
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findByNameAndCreatedByUserID(blockName, userId);

        if (fetchedBlock.isPresent()) {
            TrainingBlock block = fetchedBlock.get();

            User user = userService.getById(block.getCreatedByUserID());
            user.updateBlockPosition(block.getName());
            userService.save(user);
            return block;
        } else {
            throw new NotFoundException();
        }
    }


    public TrainingBlock get(ObjectId id) throws Exception {
        if (id == null) {
            throw new RuntimeException("Block ID is required.");
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findById(id);

        if (fetchedBlock.isPresent()) {
            TrainingBlock block = fetchedBlock.get();

            User user = userService.getById(block.getCreatedByUserID());
            user.updateBlockPosition(block.getName());
            userService.save(user);

            return block;
        } else {
            throw new NotFoundException();
        }
    }


    public void create(CreateTrainingBlockRequest createBlockRequest) throws Exception{

        if(createBlockRequest.getUserId() == null) {
            throw new RuntimeException("UserID is required.");
        }

        TrainingBlock block = new TrainingBlock(createBlockRequest.getBlockName(), createBlockRequest.getUserId(), createBlockRequest.getSortedDays(), false);

        try {
            blockRepository.save(block);
            userService.addBlock(block);
        } catch (Exception e) {
            blockRepository.delete(block);
            throw e;
        }

    }

    public void update(UpdateBlockRequest updateBlockRequest) throws Exception{
        ObjectId id = updateBlockRequest.getId();

        if(id == null) {
            throw new RuntimeException("Block ID and Block Name is required.");
        }

        try {
            TrainingBlock block = this.get(id);
            block.setName(updateBlockRequest.getName());
            block.setWeeks(updateBlockRequest.getWeeks());
            block.setMostRecentWeekOpen(updateBlockRequest.getWeekIndex());
            block.setMostRecentDayOpen(updateBlockRequest.getDayIndex());
            blockRepository.save(block);

            User user = userService.getById(block.getCreatedByUserID());
            user.updateBlockPosition(block.getName());
            userService.save(user);
        } catch (Exception e) {
            throw e;
        }
    }

    public void log(UpdateBlockRequest updateBlockRequest) throws Exception{
        ObjectId id = updateBlockRequest.getId();

        if(id == null) {
            throw new RuntimeException("Block ID and Block Name is required.");
        }

        try {
            TrainingBlock block = this.get(id);
            block.setName(updateBlockRequest.getName());
            block.setWeeks(updateBlockRequest.getWeeks());
            block.setMostRecentWeekOpen(updateBlockRequest.getWeekIndex());
            block.setMostRecentDayOpen(updateBlockRequest.getDayIndex());
            block.setLogged(true);
            blockRepository.save(block);

            User user = userService.getById(block.getCreatedByUserID());
            user.updateBlockPosition(block.getName());
            userService.save(user);
        } catch (Exception e) {
            throw e;
        }
    }

    public void delete(DeleteBlockRequest deleteBlockRequest) throws Exception{
        String blockName = deleteBlockRequest.getBlockName();
        ObjectId userId = deleteBlockRequest.getUserId();

        if(userId == null || blockName == null) {
            throw new RuntimeException("Block ID and Block Name is required.");
        }

        try {
            TrainingBlock block = this.get(blockName, userId);
            blockRepository.delete(block);

            User user = userService.getById(block.getCreatedByUserID());
            user.deleteBlock(block.getName());
            userService.save(user);
        } catch (Exception e) {
            throw e;
        }
    }


     public void rename(RenameBlockRequest renameBlockRequest) throws Exception{
        String blockName = renameBlockRequest.getBlockName();
        String newName = renameBlockRequest.getNewName();
        ObjectId userId = renameBlockRequest.getUserId();

        if(userId == null || blockName == null || newName == null) {
            throw new RuntimeException("User ID, Block Name, and New Name are required.");
        }

        try {
            TrainingBlock block = this.get(blockName, userId);
            User user = userService.getById(block.getCreatedByUserID());

            List<String> blocks = user.getTrainingBlockNames();
            if (blocks.contains(newName)){
                throw new Exception("409");
            }
            block.setName(newName);
            blockRepository.save(block);

            user.renameBlock(blockName, newName);
            userService.save(user);
        } catch (Exception e) {
            throw e;
        }
    }

}
