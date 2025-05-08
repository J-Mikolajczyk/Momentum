package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class CreateTrainingBlockRequest {

    private final String blockName;
    private final ObjectId userId;
    private final int dayAmount;

    public CreateTrainingBlockRequest() {
        this.blockName = null;
        this.userId = null;
        this.dayAmount = -1;
    }

    public CreateTrainingBlockRequest(String blockName, ObjectId userId, int dayAmount) {
        this.blockName = blockName;
        this.userId = userId;
        this.dayAmount = dayAmount;
    }

    public String getBlockName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public int getDayAmount() {
        return dayAmount;
    }
}
