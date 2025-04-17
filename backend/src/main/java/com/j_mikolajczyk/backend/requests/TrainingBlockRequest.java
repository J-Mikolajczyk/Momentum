package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class TrainingBlockRequest {

    private final String blockName;
    private final ObjectId userId;

    public TrainingBlockRequest() {
        this.blockName = null;
        this.userId = null;
    }

    public TrainingBlockRequest(String blockName, ObjectId userId) {
        this.blockName = blockName;
        this.userId = userId;
    }

    public String getName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    }
}
