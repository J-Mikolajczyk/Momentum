package com.j_mikolajczyk.backend.dto;
import java.util.List;

import com.j_mikolajczyk.backend.models.TrainingBlock;
import com.j_mikolajczyk.backend.models.Week;

public class BlockDTO {

    private String id;
    private String name;
    private List<Week> weeks;
    private String createdByUserID;

    public BlockDTO(TrainingBlock trainingBlock) {
        this.id = trainingBlock.getId().toString();
        this.name = trainingBlock.getName();    
        this.weeks = trainingBlock.getWeeks();
        this.createdByUserID = trainingBlock.getCreatedByUserID().toString();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Week> getWeeks() {
        return this.weeks;
    }

    public String getCreatedByUserID() {
        return this.createdByUserID;
    }
    
}
