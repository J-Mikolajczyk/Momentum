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
    private int dayAmount;


    public TrainingBlock() {
    }

    public TrainingBlock(String name, List<Week> weeks, ObjectId createdByUserID, int dayAmount) {
        this.name = name;
        this.weeks = weeks;
        this.createdByUserID = createdByUserID;
        this.dayAmount = dayAmount;
    }

    public TrainingBlock(String name, ObjectId createdByUserID, int dayAmount) {
        this.name = name;
        this.weeks = new ArrayList<Week>();
        this.weeks.add(new Week(dayAmount));
        this.createdByUserID = createdByUserID;
        this.dayAmount = dayAmount;
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

    public int getDayAmount() {
        return this.dayAmount;
    }

    public void setDayAmount(int dayAmount) {
        this.dayAmount = dayAmount;
    }

    public void addWeek(Week week) {
        this.weeks.add(week);
    }
    
}
