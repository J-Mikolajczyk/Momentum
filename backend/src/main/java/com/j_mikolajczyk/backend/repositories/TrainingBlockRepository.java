package com.j_mikolajczyk.backend.repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.j_mikolajczyk.backend.models.TrainingBlock;

@Repository
public interface TrainingBlockRepository extends MongoRepository<TrainingBlock, ObjectId> {
    Optional<TrainingBlock> findByIdAndCreatedByUserID(ObjectId id, ObjectId createdByUserID);
}
