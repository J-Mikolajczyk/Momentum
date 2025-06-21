package com.j_mikolajczyk.backend.services;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import com.j_mikolajczyk.backend.controllers.TrainingBlockController;
import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.User;
import com.j_mikolajczyk.backend.repositories.TrainingBlockRepository;
import com.j_mikolajczyk.backend.requests.UpdateBlockRequest;
import com.j_mikolajczyk.backend.requests.CreateTrainingBlockRequest;
import com.j_mikolajczyk.backend.requests.RenameBlockRequest;
import com.j_mikolajczyk.backend.requests.TrainingBlockRequest;

@Service
public class TrainingBlockService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingBlockService.class);

    private final TrainingBlockRepository blockRepository;
    private final UserService userService;

    @Autowired
    public TrainingBlockService(TrainingBlockRepository blockRepository, @Lazy UserService userService) {
        this.blockRepository = blockRepository;
        this.userService = userService;
    }

    public TrainingBlock get(String blockName, ObjectId id) throws Exception {

        ObjectId userId;
        try {
            User user = userService.getById(id);
            userId = user.getId();
        } catch(Exception e) {
            logger.warn("User '{}' not found while fetching block '{}'", id, blockName);
            throw new NotFoundException();
        }

        Optional<TrainingBlock> fetchedBlock = blockRepository.findByNameAndCreatedByUserID(blockName, userId);

        if (fetchedBlock.isPresent()) {
            TrainingBlock block = fetchedBlock.get();
            logger.info("Block '{}' successfully retrieved for user '{}'", blockName, userId);

            User user = userService.getById(block.getCreatedByUserID());
            user.updateBlockPosition(block.getName());
            userService.save(user);
            return block;
        } else {
            logger.warn("Block '{}' not found for user '{}'", blockName, userId);
            throw new NotFoundException();
        }
    }

    public TrainingBlock get(ObjectId id) throws Exception {

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
        String blockName = createBlockRequest.getBlockName();
        ObjectId userId = createBlockRequest.getUserId();

        TrainingBlock block = new TrainingBlock(blockName, userId, createBlockRequest.getSortedDays(), false);

        try {
            blockRepository.save(block);
            userService.addBlock(block);
            logger.info("Block '{}' created for user '{}'", blockName, userId);
        } catch (Exception e) {
            blockRepository.delete(block);
            logger.error("Error creating block '{}': {}", blockName, e.getMessage());
            throw e;
        }

    }

    public void update(UpdateBlockRequest updateBlockRequest) throws Exception{
        ObjectId id = updateBlockRequest.getId();

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
            logger.info("Updating block '{}' for user '{}'", updateBlockRequest.getName(), user.getId());
        } catch (Exception e) {
            logger.error("Error updating block '{}': {}", id, e.getMessage());
            throw e;
        }
    }

    public void log(UpdateBlockRequest updateBlockRequest) throws Exception{
        ObjectId id = updateBlockRequest.getId();

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
            logger.info("Logging block '{}' for user '{}'", id, block.getCreatedByUserID());
        } catch (Exception e) {
            logger.error("Error logging block '{}': {}", id, e.getMessage());
            throw e;
        }
    }

    public void delete(TrainingBlockRequest deleteBlockRequest) throws Exception {
        String blockName = deleteBlockRequest.getBlockName();
        ObjectId userId = deleteBlockRequest.getUserId();

        try {
            TrainingBlock block = this.get(blockName, userId);
            blockRepository.delete(block);

            User user = userService.getById(block.getCreatedByUserID());
            user.deleteBlock(block.getName());
            userService.save(user);
            logger.info("Deleting block '{}' for user '{}'", blockName, userId);
        } catch (Exception e) {
            logger.error("Error deleting block '{}': {}.", blockName, e.getMessage());
            throw e;
        }
    }

    public void rename(RenameBlockRequest renameBlockRequest) throws Exception{
        String blockName = renameBlockRequest.getBlockName();
        String newName = renameBlockRequest.getNewName();
        ObjectId userId = renameBlockRequest.getUserId();

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
            logger.info("Renamed block '{}' for user '{}'", blockName, userId);
        } catch (Exception e) {
            logger.error("Error renaming block '{}': {}", blockName, e.getMessage());
            throw e;
        }
    }

    public void deleteAllForUser(ObjectId userId) {
        blockRepository.deleteByCreatedByUserID(userId);
    }

}
