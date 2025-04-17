package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class CreateTrainingBlockRequest {

    private final String blockName;
    private final ObjectId userId;

    public CreateTrainingBlockRequest() {
        this.blockName = null;
        this.userId = null;
    }

    public CreateTrainingBlockRequest(String blockName, ObjectId userId) {
        this.blockName = blockName;
        this.userId = userId;
    }

    public String getBlockName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    }
}
