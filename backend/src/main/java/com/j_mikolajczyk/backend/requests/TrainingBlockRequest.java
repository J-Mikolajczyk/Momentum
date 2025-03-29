package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class TrainingBlockRequest {

    private final ObjectId blockId;
    private final ObjectId userId;

    public TrainingBlockRequest() {
        this.blockId = null;
        this.userId = null;
    }

    public TrainingBlockRequest(ObjectId blockId, ObjectId userId) {
        this.blockId = blockId;
        this.userId = userId;
    }

    public ObjectId getBlockId() {
        return blockId;
    }

    public ObjectId getUserId() {
        return userId;
    }
}
