package com.j_mikolajczyk.backend.requests;
import java.util.List;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.models.Week;

public class UpdateBlockRequest {

    private ObjectId id;
    private String name;
    private List<Week> weeks;
    private int weekIndex;
    private int dayIndex;

    public UpdateBlockRequest() {
        this.id = null;
        this.name = null;
        this.weeks = null;
        this.weekIndex = -1;
        this.dayIndex = -1;
    }

    public UpdateBlockRequest(String id, String name, List<Week> weeks, int weekIndex, int dayIndex) {
        this.id = new ObjectId(id);
        this.name = name;
        this.weeks = weeks;
        this.weekIndex = weekIndex;
        this.dayIndex = dayIndex;
    }

    public ObjectId getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }


    public List<Week> getWeeks() {
        return this.weeks;
    }

    public int getWeekIndex() {
        return this.weekIndex;
    }

    public int getDayIndex() {
        return this.dayIndex;
    }
    
}
