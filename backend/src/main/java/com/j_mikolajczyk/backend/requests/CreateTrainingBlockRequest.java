package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class CreateTrainingBlockRequest {

    private final String name;
    private final ObjectId userId;

    public CreateTrainingBlockRequest() {
        this.name = null;
        this.userId = null;
    }

    public CreateTrainingBlockRequest(String name, ObjectId userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public ObjectId getUserId() {
        return userId;
    }
}
