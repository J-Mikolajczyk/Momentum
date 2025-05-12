package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class DeleteBlockRequest {
    private final String blockName;
    private final ObjectId userId;

    public DeleteBlockRequest(String blockName, ObjectId userId) {
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
