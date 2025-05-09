package com.j_mikolajczyk.backend.models;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "training-blocks")
public class TrainingBlock {

    @Id
    private ObjectId id;
    private String name;
    private List<Week> weeks;
    private ObjectId createdByUserID;
    private List<String> selectedDays;


    public TrainingBlock() {
    }

    public TrainingBlock(String name, List<Week> weeks, ObjectId createdByUserID, List<String> selectedDays) {
        this.name = name;
        this.weeks = weeks;
        this.createdByUserID = createdByUserID;
        this.selectedDays = selectedDays;
    }

    public TrainingBlock(String name, ObjectId createdByUserID, List<String> selectedDays) {
        this.name = name;
        this.weeks = new ArrayList<Week>();
        this.weeks.add(new Week(selectedDays));
        this.createdByUserID = createdByUserID;
        this.selectedDays = selectedDays;
    }

    public ObjectId getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Week> getWeeks() {
        return this.weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public ObjectId getCreatedByUserID() {
        return this.createdByUserID;
    }

    public void setCreatedByUserID(ObjectId createdByUserID) {
        this.createdByUserID = createdByUserID;
    }

    public List<String> getSelectedDays() {
        return this.selectedDays;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }
    
}
