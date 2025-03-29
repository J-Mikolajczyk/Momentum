package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class CreateBlockRequest {

    private final String name;
    private final ObjectId userId;

    public CreateBlockRequest() {
        this.name = null;
        this.userId = null;
    }

    public CreateBlockRequest(String name, ObjectId userId) {
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
