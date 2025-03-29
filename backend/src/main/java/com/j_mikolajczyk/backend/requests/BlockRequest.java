package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class BlockRequest {

    private final ObjectId blockId;
    private final ObjectId userId;

    public BlockRequest() {
        this.blockId = null;
        this.userId = null;
    }

    public BlockRequest(ObjectId blockId, ObjectId userId) {
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
