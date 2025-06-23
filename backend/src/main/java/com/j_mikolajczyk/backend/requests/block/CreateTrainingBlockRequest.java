package com.j_mikolajczyk.backend.requests.block;

import java.util.List;
import java.util.ArrayList;
import org.bson.types.ObjectId;

public class CreateTrainingBlockRequest {

    private final String blockName;
    private final ObjectId userId;
    private final List<String> sortedDays;

    public CreateTrainingBlockRequest() {
        this.blockName = null;
        this.userId = null;
        this.sortedDays = null;
    }

    public CreateTrainingBlockRequest(String blockName, ObjectId userId, List<String> sortedDays) {
        this.blockName = blockName;
        this.userId = userId;
        this.sortedDays = sortedDays;
    }

    public String getBlockName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public List<String> getSortedDays() {
        return sortedDays;
    }
}
