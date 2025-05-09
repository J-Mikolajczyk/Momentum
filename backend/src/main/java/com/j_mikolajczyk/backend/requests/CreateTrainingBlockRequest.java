package com.j_mikolajczyk.backend.requests;

import java.util.List;

import org.bson.types.ObjectId;

public class CreateTrainingBlockRequest {

    private final String blockName;
    private final ObjectId userId;
    private final List<String> selectedDays;

    public CreateTrainingBlockRequest() {
        this.blockName = null;
        this.userId = null;
        this.selectedDays = null;
    }

    public CreateTrainingBlockRequest(String blockName, ObjectId userId, List<String> selectedDays) {
        this.blockName = blockName;
        this.userId = userId;
        this.selectedDays = selectedDays;
    }

    public String getBlockName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }
}
