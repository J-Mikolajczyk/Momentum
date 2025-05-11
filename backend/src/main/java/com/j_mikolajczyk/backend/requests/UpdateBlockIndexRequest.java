package com.j_mikolajczyk.backend.requests;

import org.bson.types.ObjectId;

public class UpdateBlockIndexRequest {
    private final String blockName;
    private final ObjectId userId;
    private final int weekIndex;
    private final int dayIndex;

    public UpdateBlockIndexRequest(String blockName, ObjectId userId, int weekIndex, int dayIndex) {
        this.blockName = blockName;
        this.userId = userId;
        this.weekIndex = weekIndex;
        this.dayIndex = dayIndex;
    }

    public String getBlockName() {
        return blockName;
    }

    public ObjectId getUserId() {
        return userId;
    } 

    public int getWeekIndex() {
        return weekIndex;
    }

    public int getDayIndex() {
        return dayIndex;
    }


}
