package com.j_mikolajczyk.backend.requests;
import java.util.List;

import org.bson.types.ObjectId;

import com.j_mikolajczyk.backend.models.Week;

public class UpdateBlockRequest {

    private ObjectId id;
    private String name;
    private List<Week> weeks;

    public UpdateBlockRequest() {
    }

    public UpdateBlockRequest(String id, String name, List<Week> weeks) {
        this.id = new ObjectId(id);
        this.name = name;
        this.weeks = weeks;
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
    
}
