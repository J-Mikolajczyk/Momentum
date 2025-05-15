package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class RenameBlockRequest {
    private final String blockName;
    private final String newName;
    private final ObjectId userId;

    public RenameBlockRequest(String blockName, String newName, ObjectId userId) {
        this.blockName = blockName;
        this.newName = newName;
        this.userId = userId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String getNewName() {
        return newName;
    }

    public ObjectId getUserId() {
        return userId;
    } 

}
